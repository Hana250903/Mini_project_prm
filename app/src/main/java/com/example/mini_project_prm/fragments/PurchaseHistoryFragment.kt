package com.example.mini_project_prm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project_prm.R
import com.example.mini_project_prm.adapters.OrderAdapter
import com.example.mini_project_prm.api.RetrofitClient
import com.example.mini_project_prm.models.Order
import kotlinx.coroutines.launch

class PurchaseHistoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private var orders: List<Order> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_purchase_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewOrders)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchOrders()
    }

    private fun fetchOrders() {
        val userId = 1 // <-- Thay bằng ID thực tế của user đã đăng nhập

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getOrderById("eq.${userId}")
                orders = response
                orderAdapter = OrderAdapter(orders, viewLifecycleOwner.lifecycleScope)
                recyclerView.adapter = orderAdapter
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Không thể tải đơn hàng: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}