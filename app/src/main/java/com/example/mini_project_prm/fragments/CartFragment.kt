package com.example.mini_project_prm.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project_prm.R
import com.example.mini_project_prm.adapters.CartAdapter
import com.example.mini_project_prm.models.CartManager
import com.example.mini_project_prm.zalo_pay.OrderPayment
import java.text.NumberFormat
import java.util.Locale
import android.widget.Toast
import com.example.mini_project_prm.models.CartItem

class CartFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var tvCartCount: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var btnCheckout: Button

    private var userId: Int = -1

    companion object {
        fun newInstance(userId: Int): CartFragment {
            val fragment = CartFragment()
            val args = Bundle().apply {
                putInt("userId", userId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        // Ánh xạ các View từ layout
        recyclerView = view.findViewById(R.id.recyclerViewCart)
        tvCartCount = view.findViewById(R.id.tvCartCount)
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice)
        btnCheckout = view.findViewById(R.id.btnCheckout)

        userId = arguments?.getInt("userId", -1) ?: -1

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val itemsFromManager = CartManager.getCartItems()

        cartAdapter = CartAdapter(itemsFromManager) {
            // Đây là nơi xử lý khi giỏ hàng được cập nhật từ adapter
            // Ta sẽ gọi lại các hàm cập nhật giống như trong onResume
            this.cartAdapter.notifyDataSetChanged()
            updateCartCount()
            updateTotalPrice()
        }
        recyclerView.adapter = cartAdapter

        updateCartCount()
        updateTotalPrice()

        btnCheckout.setOnClickListener {
            val cartItems = CartManager.getCartItems()

            // Kiểm tra nếu giỏ hàng trống thì không làm gì cả
            if (cartItems.isEmpty()) {
                Toast.makeText(requireContext(), "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val totalItems = cartItems.sumOf { it.quantity }
            val totalPrice = CartManager.getTotalPrice()

            // Tạo tên sản phẩm tóm tắt cho màn hình thanh toán
            val summaryProductName = if (cartItems.size == 1) {
                // Nếu chỉ có 1 loại sản phẩm, hiện tên của nó
                cartItems.first().name
            } else {
                // Nếu có nhiều loại, hiện tên tóm tắt
                "Thanh toán cho ${cartItems.size} loại sản phẩm"
            }

            // Tạo Intent và truyền đầy đủ dữ liệu
            val intent = Intent(requireContext(), OrderPayment::class.java).apply {
                putExtra("soluong", totalItems.toString())
                putExtra("total", totalPrice)
                // Thêm dữ liệu mới để khớp với UI của OrderPayment
                putExtra("productName", summaryProductName)
                putExtra("price", totalPrice) // Giá tạm tính cũng là tổng giá
                putExtra("userId", userId)
            }
            startActivity(intent)
        }

        return view
    }

    private fun updateCartCount() {
        tvCartCount.text = "${CartManager.getCartItems().size} Sản phẩm"
    }

    fun updateTotalPrice() {
        val total = CartManager.getTotalPrice() // Lấy tổng giá từ Manager
        val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(total)
        tvTotalPrice.text = "${formattedPrice}đ"
    }
}