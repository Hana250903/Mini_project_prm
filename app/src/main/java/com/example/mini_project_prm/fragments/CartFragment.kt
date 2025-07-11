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
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var tvCartCount: TextView
    private lateinit var tvTotalPrice: TextView

    private val cartItems = mutableListOf(
        CartItem(
            "Nendoroid 2576 Alisa...",
            "Tặng kèm 01 card trong suốt",
            1550000.0,
            1700000.0,
            1,
            R.drawable.ic_launcher_background
        ),
        CartItem(
            "Hatsune Miku Wonderland...",
            "Thanh toán toàn bộ",
            430000.0,
            530000.0,
            1,
            R.drawable.ic_launcher_background
        )
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewCart)
        tvCartCount = view.findViewById(R.id.tvCartCount)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(cartItems)
        recyclerView.adapter = cartAdapter

        tvTotalPrice = view.findViewById(R.id.tvTotalPrice)

        updateCartCount()
        updateTotalPrice()

        return view
    }

    private fun updateCartCount() {
        tvCartCount.text = "${cartItems.size} Sản phẩm"
    }

    fun updateTotalPrice() {
        var total = 0.0
        for (item in cartItems) {
            total += item.priceSale * item.quantity
        }
        val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(total)
        tvTotalPrice.text = "${formattedPrice}đ"
    }
}
