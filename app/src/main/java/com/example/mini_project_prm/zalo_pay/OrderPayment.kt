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
import com.example.mini_project_prm.R
import com.example.mini_project_prm.zalo_pay.api.CreateOrder
import org.json.JSONObject
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import java.text.NumberFormat
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
            val orderApi = CreateOrder()
            try {
                // Gọi createOrder, và vì nó trả về JSONObject? nên cần xử lý null
                val data: JSONObject? = orderApi.createOrder(totalString)

                if (data != null) { // Kiểm tra nếu data không null
                    val code = data.getString("return_code")

                    if (code == "1") {
                        val token = data.getString("zp_trans_token")
                        Log.d("ZaloPayDebug", "return_code: $code | token: $token")

                        ZaloPaySDK.getInstance().payOrder(this@OrderPayment, token, "demozpdk://app", object : PayOrderListener {
                            override fun onPaymentSucceeded(transactionId: String, transToken: String, appTransID: String) {
                                Log.d("ZaloPayDebug", "Payment succeeded: $transactionId")
                                val intent1 = Intent(this@OrderPayment, PaymentNotification::class.java)
                                intent1.putExtra("result", "Thanh toán thành công")
                                intent1.putExtra("total", "Bạn đã thanh toán $totalFormatted")
                                startActivity(intent1)
                            }

                            override fun onPaymentCanceled(transToken: String, appTransID: String) {
                                Log.d("ZaloPayDebug", "Payment canceled")
                                val intent2 = Intent(this@OrderPayment, PaymentNotification::class.java)
                                intent2.putExtra("result", "Thanh toán đã được hủy")
                                startActivity(intent2)
                            }

                            override fun onPaymentError(zaloPayError: ZaloPayError, transToken: String, appTransID: String) {
                                Log.e("ZaloPayDebug", "Payment error: ${zaloPayError.toString()}")
                                val intent3 = Intent(this@OrderPayment, PaymentNotification::class.java)
                                intent3.putExtra("result", "Lỗi thanh toán")
                                startActivity(intent3)
                            }
                        })
                    } else {
                        // Xử lý các mã lỗi khác từ ZaloPay API nếu cần
                        Log.e("ZaloPayDebug", "ZaloPay API returned error code: $code, message: ${data.optString("return_message")}")
                        val errorMsg = data.optString("return_message", "Có lỗi xảy ra khi tạo đơn hàng.")
                        val intentErr = Intent(this@OrderPayment, PaymentNotification::class.java)
                        intentErr.putExtra("result", "Lỗi tạo đơn hàng")
                        intentErr.putExtra("message", errorMsg)
                        startActivity(intentErr)
                    }
                } else {
                    // Xử lý trường hợp data trả về từ createOrder là null (ví dụ: lỗi mạng)
                    Log.e("ZaloPayDebug", "API createOrder returned null data.")
                    val intentNull = Intent(this@OrderPayment, PaymentNotification::class.java)
                    intentNull.putExtra("result", "Lỗi kết nối hoặc dữ liệu")
                    intentNull.putExtra("message", "Không thể tạo đơn hàng, vui lòng thử lại.")
                    startActivity(intentNull)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ZaloPayDebug", "Exception during order creation or ZaloPay SDK call", e)
                val intentEx = Intent(this@OrderPayment, PaymentNotification::class.java)
                intentEx.putExtra("result", "Lỗi hệ thống")
                intentEx.putExtra("message", "Đã xảy ra lỗi không mong muốn.")
                startActivity(intentEx)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }
}