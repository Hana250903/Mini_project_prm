package com.example.mini_project_prm.zalo_pay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mini_project_prm.MainActivity
import com.example.mini_project_prm.R

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
        val intent = intent // Lấy Intent đã khởi chạy Activity này

        val result = intent.getStringExtra("result")
        val totalText = intent.getStringExtra("total") // Có thể là null

        tvNotify.text = result ?: "Không có thông báo" // Hiển thị thông báo, hoặc chuỗi mặc định nếu null

        // Cập nhật tvTotal
        tvTotal.text = totalText ?: "" // Hiển thị tổng tiền, hoặc chuỗi rỗng nếu null

        // Cập nhật icon dựa trên kết quả
        when (result) {
            "Thanh toán thành công" -> imgPaymentStatus.setImageResource(R.drawable.ic_success)
            "Thanh toán đã được hủy" -> imgPaymentStatus.setImageResource(R.drawable.ic_cancel)
            else -> imgPaymentStatus.setImageResource(R.drawable.ic_error)
        }
    }

    private fun setupButtonListener() {
        btnReturn.setOnClickListener {
            val intentReturn = Intent(this.applicationContext, MainActivity::class.java)
            startActivity(intentReturn)
            finish() // Tùy chọn: Kết thúc Activity hiện tại để người dùng không thể quay lại bằng nút Back
        }
    }
}