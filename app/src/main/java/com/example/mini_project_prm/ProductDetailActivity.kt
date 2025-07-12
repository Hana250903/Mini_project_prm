package com.example.mini_project_prm

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mini_project_prm.models.Figure
import java.text.NumberFormat
import java.util.Locale
import androidx.appcompat.widget.Toolbar
import com.example.mini_project_prm.models.CartManager

class ProductDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val toolbar: Toolbar = findViewById(R.id.toolbarDetail)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Chi tiết sản phẩm"

        // Ánh xạ các Views từ layout
        val ivProductImage: ImageView = findViewById(R.id.ivProductImageDetail)
        val tvProductName: TextView = findViewById(R.id.tvProductNameDetail)
        val tvProductPrice: TextView = findViewById(R.id.tvProductPriceDetail)
        val tvProductDescription: TextView = findViewById(R.id.tvProductDescription)

        // Lấy dữ liệu Figure đã được gửi qua Intent
        val figure = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EXTRA_FIGURE_DATA", Figure::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Figure>("EXTRA_FIGURE_DATA")
        }

        // Hiển thị dữ liệu lên UI nếu figure không null
        figure?.let { currentFigure ->
            ivProductImage.setImageResource(currentFigure.imageUrl)
            tvProductName.text = currentFigure.name

            val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(currentFigure.price)
            tvProductPrice.text = "${formattedPrice}đ"

            tvProductDescription.text = currentFigure.description ?: "Không có mô tả cho sản phẩm này."

            // =======================================================
            // BẮT ĐẦU PHẦN CODE THÊM MỚI ĐỂ XỬ LÝ NÚT
            // =======================================================

            // 1. Ánh xạ các nút và text view số lượng
            val tvQuantity: TextView = findViewById(R.id.tvQuantityDetail)
            val btnDecrease: Button = findViewById(R.id.btnDecreaseQuantity)
            val btnIncrease: Button = findViewById(R.id.btnIncreaseQuantity)
            val btnAddToCart: Button = findViewById(R.id.btnAddToCartDetail)

            // 2. Xử lý logic tăng/giảm số lượng
            var currentQuantity = 1
            tvQuantity.text = currentQuantity.toString()

            btnIncrease.setOnClickListener {
                currentQuantity++
                tvQuantity.text = currentQuantity.toString()
            }

            btnDecrease.setOnClickListener {
                if (currentQuantity > 1) {
                    currentQuantity--
                    tvQuantity.text = currentQuantity.toString()
                }
            }

            // 3. Xử lý sự kiện nhấn nút "Thêm vào giỏ hàng"
            btnAddToCart.setOnClickListener {
                // Gọi hàm addItem từ CartManager
                CartManager.addItem(currentFigure, currentQuantity)

                // Hiển thị thông báo cho người dùng
                Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show()
            }

            // =======================================================
            // KẾT THÚC PHẦN CODE THÊM MỚI
            // =======================================================
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}