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
import com.google.android.material.appbar.MaterialToolbar // <-- Import Toolbar

class OrderPayment : AppCompatActivity() {
    private lateinit var tvSoluong: TextView
    private lateinit var tvTongTien: TextView
    private lateinit var btnThanhToan: Button
    private lateinit var tvProductName: TextView // Thêm biến cho tên sản phẩm
    private lateinit var tvGia: TextView // Thêm biến cho giá

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge đã được tích hợp trong theme, không cần gọi lại
        setContentView(R.layout.activity_order_payment)

        // === BƯỚC 1: XỬ LÝ TOOLBAR MỚI ===
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            // Khi nhấn nút back, đóng Activity này lại
            finish()
        }
        // ================================

        // Ánh xạ các View
        tvSoluong = findViewById(R.id.tvSoLuong)
        tvTongTien = findViewById(R.id.tvTongTien)
        btnThanhToan = findViewById(R.id.btnThanhToan)
        tvProductName = findViewById(R.id.tvProductName) // Ánh xạ view mới
        tvGia = findViewById(R.id.tvGia) // Ánh xạ view mới


        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        ZaloPaySDK.init(com.example.mini_project_prm.api.AppInfo.APP_ID, Environment.SANDBOX)

        // Lấy dữ liệu từ Intent
        val intent = intent
        tvSoluong.text = intent.getStringExtra("soluong")
        val total = intent.getDoubleExtra("total", 0.0)

        // === BƯỚC 2: HIỂN THỊ THÊM THÔNG TIN (NÊN LÀM) ===
        // Lưu ý: Bạn cần truyền thêm "productName" và "price" vào Intent khi khởi chạy Activity này
        val productName = intent.getStringExtra("productName") ?: "Sản phẩm"
        val price = intent.getDoubleExtra("price", 0.0)

        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        tvProductName.text = productName
        tvGia.text = formatter.format(price)
        // ===============================================

        val totalString = String.format("%.0f", total)
        val totalFormatted = formatter.format(total)
        tvTongTien.text = totalFormatted

        btnThanhToan.setOnClickListener {
            // Code xử lý thanh toán của bạn giữ nguyên
            createOrderInDatabase(total) { orderId ->
                Log.d("Order", "Order ID: $orderId")
                if (orderId != null) {
                    createOrderItems(orderId)
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


// trong file zalo_pay/OrderPayment.kt

    private fun createOrderItems(orderId: Int) {
        val cartItems = CartManager.getCartItems()
        lifecycleScope.launch {
            cartItems.forEach { item ->
                // Tạo đối tượng OrderItem đơn giản hơn, không cần 'figure' hay 'imageResId'
                val orderItem = OrderItem(
                    orderId = orderId,
                    figureId = item.figureId,
                    quantity = item.quantity,
                    unitPrice = item.priceSale
                )
                try {
                    val response = RetrofitClient.instance.createOrderItem(orderItem)
                    if (response.isSuccessful) {
                        Log.d("OrderItem", "Item saved for order $orderId")
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