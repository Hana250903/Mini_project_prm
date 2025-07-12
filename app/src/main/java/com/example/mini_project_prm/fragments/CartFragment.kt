package com.example.mini_project_prm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project_prm.R
import com.example.mini_project_prm.adapters.CartAdapter
import com.example.mini_project_prm.models.CartManager
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var tvCartCount: TextView
    private lateinit var tvTotalPrice: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        // === PHẦN BẠN CẦN BỔ SUNG ===
        // Ánh xạ các View từ layout
        recyclerView = view.findViewById(R.id.recyclerViewCart)
        tvCartCount = view.findViewById(R.id.tvCartCount)
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice)
        // ============================

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // LẤY DỮ LIỆU TỪ CARTMANAGER
        val itemsFromManager = CartManager.getCartItems()

        cartAdapter = CartAdapter(itemsFromManager) {
            // Đây là nơi xử lý khi giỏ hàng được cập nhật từ adapter
            updateCartCount()
            updateTotalPrice()
        }
        recyclerView.adapter = cartAdapter

        updateCartCount()
        updateTotalPrice()

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

    // Thêm hàm onResume để giỏ hàng luôn được cập nhật khi quay lại tab này
    override fun onResume() {
        super.onResume()
        // Cập nhật lại danh sách và giao diện
        cartAdapter.notifyDataSetChanged()
        updateCartCount()
        updateTotalPrice()
    }
}