package com.example.mini_project_prm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project_prm.R
import com.example.mini_project_prm.adapters.CartAdapter
import com.example.mini_project_prm.api.RetrofitClient
import com.example.mini_project_prm.models.CartItem
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var tvCartCount: TextView
    private lateinit var tvTotalPrice: TextView

//    private val cartItems = mutableListOf<CartItem>(
//        CartItem(
//            figureId = 1,
//            name = "Nendoroid 2576 Alisa...",
//            description = "Tặng kèm 01 card trong suốt",
//            priceSale = 1550000.0,
//            priceOriginal = 1700000.0,
//            imageUrl = R.drawable.ic_launcher_background,
//            quantity = 1
//        ),
//        CartItem(
//            figureId = 2,
//            name = "Hatsune Miku Wonderland...",
//            description = "Thanh toán toàn bộ",
//            priceSale = 430000.0,
//            priceOriginal = 530000.0,
//            imageUrl = R.drawable.ic_launcher_background,
//            quantity = 1
//        )
//    )

    private val cartItems = mutableListOf<CartItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewCart)
        tvCartCount = view.findViewById(R.id.tvCartCount)
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(cartItems)
        recyclerView.adapter = cartAdapter

//        updateCartCount()
//        updateTotalPrice()

        fetchCartItems()

        return view
    }

    private fun fetchCartItems() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getCartItems()
                cartItems.clear()
                cartItems.addAll(response)
                cartAdapter.notifyDataSetChanged()
                updateCartCount()
                updateTotalPrice()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
