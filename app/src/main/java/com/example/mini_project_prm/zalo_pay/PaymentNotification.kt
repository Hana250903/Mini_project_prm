package com.example.mini_project_prm.zalo_pay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.mini_project_prm.MainActivity
import com.example.mini_project_prm.R
import com.example.mini_project_prm.api.RetrofitClient
import com.example.mini_project_prm.models.CartManager.clearCart
import com.example.mini_project_prm.models.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaymentNotification : AppCompatActivity() {
    private lateinit var tvNotify: TextView
    private lateinit var tvTotal: TextView // Đảm bảo ID này tồn tại trong layout của bạn
    private lateinit var btnReturn: Button
    private lateinit var imgPaymentStatus: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment_notification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupContent()
        setupButtonListener()
    }

    private fun initViews() {
        tvNotify = findViewById(R.id.tvNotify)
        tvTotal = findViewById(R.id.tvTotal) // Đảm bảo ID này tồn tại
        btnReturn = findViewById(R.id.btnReturn)
        imgPaymentStatus = findViewById(R.id.imgPaymentStatus)
    }

    private fun setupContent() {
        val intent = intent
        val result = intent.getStringExtra("result")
        val totalText = intent.getStringExtra("total")
        val orderId = intent.getIntExtra("orderId", -1)

        tvNotify.text = result ?: "Không có thông báo"
        tvTotal.text = totalText ?: ""

        when (result) {
            "Thanh toán thành công" -> {
                imgPaymentStatus.setImageResource(R.drawable.ic_success)
                clearCart()
                updateOrderStatus(orderId, "Completed")
            }
            "Thanh toán đã được hủy" -> {
                imgPaymentStatus.setImageResource(R.drawable.ic_cancel)
                updateOrderStatus(orderId, "Cancelled")
            }
            else -> {
                imgPaymentStatus.setImageResource(R.drawable.ic_error)
                updateOrderStatus(orderId, "Failed")
            }
        }
    }


    private fun setupButtonListener() {
        btnReturn.setOnClickListener {
            val intentReturn = Intent(this.applicationContext, MainActivity::class.java)
            startActivity(intentReturn)
            finish() // Tùy chọn: Kết thúc Activity hiện tại để người dùng không thể quay lại bằng nút Back
        }
    }

    private fun updateOrderStatus(orderId: Int, newStatus: String) {
        if (orderId == -1) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.updateOrder(
                    orderId = "eq.$orderId",
                    updateBody = mapOf("status" to newStatus)
                )

                if (response.isSuccessful) {
                    Log.e("Order", "Order status updated successfully")
                } else {
                    Log.d("Order", "Failed to update order status: ${response.message()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Order", "Error: ${e.message}")
            }
        }
    }
}