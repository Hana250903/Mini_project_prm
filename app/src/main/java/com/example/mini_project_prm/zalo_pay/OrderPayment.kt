package com.example.mini_project_prm.zalo_pay

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.mini_project_prm.R
import com.example.mini_project_prm.api.RetrofitClient
import com.example.mini_project_prm.models.CartManager
import com.example.mini_project_prm.models.Order
import com.example.mini_project_prm.models.OrderItem
import com.example.mini_project_prm.zalo_pay.api.CreateOrder
import kotlinx.coroutines.launch
import org.json.JSONObject
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderPayment : AppCompatActivity() {
    private lateinit var tvSoluong: TextView
    private lateinit var tvTongTien: TextView
    private lateinit var btnThanhToan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_payment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvSoluong = findViewById(R.id.tvSoLuong)
        tvTongTien = findViewById(R.id.tvTongTien)
        btnThanhToan = findViewById(R.id.btnThanhToan)

        // Cho phép hoạt động trên luồng chính (chỉ dùng cho mục đích dev/test, không khuyến nghị cho production)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // ZaloPay SDK Init
        // Sử dụng AppInfo.APP_ID để đảm bảo tính nhất quán nếu bạn đã chuyển AppInfo sang Kotlin object
        ZaloPaySDK.init(com.example.mini_project_prm.api.AppInfo.APP_ID, Environment.SANDBOX)

        val intent = intent // 'intent' ở đây là Intent mà Activity này được khởi chạy với
        tvSoluong.text = intent.getStringExtra("soluong")
        val total = intent.getDoubleExtra("total", 0.0)

        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        val totalString = String.format("%.0f", total) // Chuỗi số nguyên để gửi đi
        val totalFormatted = formatter.format(total) // Chuỗi có định dạng tiền tệ để hiển thị
        tvTongTien.text = totalFormatted

        btnThanhToan.setOnClickListener {
            createOrderInDatabase(total) { orderId ->
                Log.d("Order", "Order ID: $orderId")
                if (orderId != null) {
                    createOrderItems(orderId)

                    // Gọi ZaloPay sau khi đã lưu DB
                    payWithZaloPay(totalString, totalFormatted)
                } else {
                    Log.e("Order", "Không tạo được đơn hàng, không thể thanh toán")
                }
            }
        }
    }

    private fun createOrderInDatabase(total: Double, onComplete: (Int?) -> Unit) {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val order = Order(
            userId = 1,
            orderDate = currentDate,
            totalAmount = total,
            status = "Pending"
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.createOrder(order)
                if (response.isSuccessful) {
                    val createdOrders = response.body()
                    val newOrderId = createdOrders?.firstOrNull()?.id
                    Log.d("Order", "Order created with ID = $newOrderId")
                    onComplete(newOrderId)
                } else {
                    Log.e("Order", "Failed to create order: ${response.code()}")
                    onComplete(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(null)
            }
        }
    }


    private fun createOrderItems(orderId: Int) {
        val cartItems = CartManager.getCartItems()
        lifecycleScope.launch {
            cartItems.forEach { item ->
                val orderItem = OrderItem(
                    orderId = orderId,
                    figureId = item.figureId, // đảm bảo CartItem có figureId
                    quantity = item.quantity,
                    unitPrice = item.priceSale,
                    imageResId = item.imageUrl
                )
                try {
                    val response = RetrofitClient.instance.createOrderItem(orderItem)
                    if (response.isSuccessful) {
                        Log.d("OrderItem", "Item saved")
                    } else {
                        Log.e("OrderItem", "Failed to save item: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("OrderItem", "Error: ${e.message}")
                }
            }
        }
    }

    private fun payWithZaloPay(totalString: String, totalFormatted: String) {
        val orderApi = CreateOrder()
        try {
            val data: JSONObject? = orderApi.createOrder(totalString)

            if (data != null && data.getString("return_code") == "1") {
                val token = data.getString("zp_trans_token")
                ZaloPaySDK.getInstance().payOrder(this@OrderPayment, token, "demozpdk://app", object : PayOrderListener {
                    override fun onPaymentSucceeded(transactionId: String, transToken: String, appTransID: String) {
                        val intent1 = Intent(this@OrderPayment, PaymentNotification::class.java)
                        intent1.putExtra("result", "Thanh toán thành công")
                        intent1.putExtra("total", "Bạn đã thanh toán $totalFormatted")
                        startActivity(intent1)
                    }

                    override fun onPaymentCanceled(transToken: String, appTransID: String) {
                        val intent2 = Intent(this@OrderPayment, PaymentNotification::class.java)
                        intent2.putExtra("result", "Thanh toán đã được hủy")
                        startActivity(intent2)
                    }

                    override fun onPaymentError(zaloPayError: ZaloPayError, transToken: String, appTransID: String) {
                        val intent3 = Intent(this@OrderPayment, PaymentNotification::class.java)
                        intent3.putExtra("result", "Lỗi thanh toán")
                        startActivity(intent3)
                    }
                })
            } else {
                val intentErr = Intent(this@OrderPayment, PaymentNotification::class.java)
                intentErr.putExtra("result", "Lỗi tạo đơn hàng ZaloPay")
                startActivity(intentErr)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }
}