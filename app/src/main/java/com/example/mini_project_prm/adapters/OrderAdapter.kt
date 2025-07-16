package com.example.mini_project_prm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project_prm.R
import com.example.mini_project_prm.api.RetrofitClient
import com.example.mini_project_prm.models.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class OrderAdapter(
    private val orders: List<Order>,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val expandedOrders = mutableSetOf<Int>()

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvTotal: TextView = view.findViewById(R.id.tvTotalAmount)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val recyclerViewItems: RecyclerView = view.findViewById(R.id.recyclerViewOrderItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvDate.text = "Ngày: ${order.orderDate}"
        holder.tvTotal.text = "Tổng: ${order.totalAmount}đ"
        holder.tvStatus.text = "Trạng thái: ${order.status}"

        val isExpanded = expandedOrders.contains(order.id)
        holder.recyclerViewItems.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            val isExpanded = expandedOrders.contains(order.id)
            if (isExpanded) {
                expandedOrders.remove(order.id)
                notifyItemChanged(position)
            } else {
                expandedOrders.add(order.id!!)
                notifyItemChanged(position)

                holder.recyclerViewItems.visibility = View.VISIBLE
                holder.recyclerViewItems.layoutManager = LinearLayoutManager(holder.itemView.context)

                // GỌI API TRONG COROUTINE
                scope.launch {
                    try {
                        val orderItems = RetrofitClient.instance.getOrderItemByOrderId("eq.${order.id}")
                        // ĐẢM BẢO GÁN ADAPTER NGAY SAU KHI CÓ DỮ LIỆU
                        holder.recyclerViewItems.adapter = OrderItemAdapter(orderItems)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = orders.size
}
