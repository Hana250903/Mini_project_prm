package com.example.mini_project_prm.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project_prm.R
import com.example.mini_project_prm.api.RetrofitClient
import com.example.mini_project_prm.models.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import android.util.Log // <-- Thêm import này

class OrderAdapter(
    private var orders: List<Order>,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val expandedOrders = mutableSetOf<Int>()

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvItemCount: TextView = view.findViewById(R.id.tvItemCount)
        val tvTotalAmount: TextView = view.findViewById(R.id.tvTotalAmount)
        val ivExpand: ImageView = view.findViewById(R.id.ivExpand)
        val recyclerViewItems: RecyclerView = view.findViewById(R.id.recyclerViewOrderItems)
        val mainLayout: ConstraintLayout = view.findViewById(R.id.mainOrderLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.tvOrderId.text = "Đơn hàng #${order.id}"
        holder.tvOrderDate.text = "Ngày đặt: ${formatDate(order.orderDate)}"
        holder.tvStatus.text = order.status?.replaceFirstChar { it.uppercase() }

        val formattedTotal = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(order.totalAmount)
        holder.tvTotalAmount.text = formattedTotal
        holder.tvItemCount.text = ""

        when (order.status?.lowercase()) {
            "completed", "hoàn thành" -> holder.tvStatus.background.setTint(Color.parseColor("#4CAF50"))
            "pending", "đang xử lý" -> holder.tvStatus.background.setTint(Color.parseColor("#2196F3"))
            "cancelled", "đã hủy" -> holder.tvStatus.background.setTint(Color.parseColor("#F44336"))
            else -> holder.tvStatus.background.setTint(Color.parseColor("#9E9E9E"))
        }

        val isExpanded = expandedOrders.contains(order.id)
        holder.recyclerViewItems.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.ivExpand.rotation = if (isExpanded) 180f else 0f

        if (isExpanded && holder.recyclerViewItems.adapter == null) {
            fetchOrderItems(holder, order)
        }

        holder.mainLayout.setOnClickListener {
            toggleExpansion(holder, order)
        }
    }

    private fun toggleExpansion(holder: OrderViewHolder, order: Order) {
        val isCurrentlyExpanded = expandedOrders.contains(order.id)
        if (isCurrentlyExpanded) {
            expandedOrders.remove(order.id)
            holder.recyclerViewItems.visibility = View.GONE
            holder.ivExpand.animate().rotation(0f).setDuration(200).start()
        } else {
            order.id?.let {
                expandedOrders.add(it)
                holder.recyclerViewItems.visibility = View.VISIBLE
                holder.ivExpand.animate().rotation(180f).setDuration(200).start()
                if (holder.recyclerViewItems.adapter == null) {
                    fetchOrderItems(holder, order)
                }
            }
        }
    }

    // === HÀM ĐÃ ĐƯỢC SỬA LỖI ===
    private fun fetchOrderItems(holder: OrderViewHolder, order: Order) {
        val TAG = "OrderDebug" // Tag để lọc log cho dễ
        scope.launch {
            try {
                val orderIdQuery = "eq.${order.id!!}"
                Log.d(TAG, "Bắt đầu gọi API lấy chi tiết cho order ID: ${order.id}")

                val response = RetrofitClient.instance.getOrderItemByOrderId(orderIdQuery)

                if (response.isSuccessful) {
                    val orderItemsList = response.body()
                    if (orderItemsList != null) {
                        if (orderItemsList.isEmpty()) {
                            Log.d(TAG, "API thành công nhưng trả về danh sách rỗng (0 sản phẩm).")
                            holder.tvItemCount.text = "0 sản phẩm"
                        } else {
                            Log.d(TAG, "API thành công, trả về ${orderItemsList.size} sản phẩm.")
                            holder.tvItemCount.text = "${orderItemsList.size} sản phẩm"
                            holder.recyclerViewItems.layoutManager = LinearLayoutManager(holder.itemView.context)
                            holder.recyclerViewItems.adapter = OrderItemAdapter(orderItemsList)
                        }
                    } else {
                        Log.e(TAG, "API thành công nhưng body trả về là null.")
                        holder.tvItemCount.text = "Không có dữ liệu"
                    }
                } else {
                    Log.e(TAG, "API gọi thất bại: Code ${response.code()} - ${response.message()}")
                    holder.tvItemCount.text = "Lỗi tải dữ liệu"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "Lỗi kết nối hoặc ngoại lệ khác: ${e.message}")
                holder.tvItemCount.text = "Lỗi kết nối"
            }
        }
    }

    override fun getItemCount(): Int = orders.size

    private fun formatDate(dateString: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString ?: "")
            date?.let { outputFormat.format(it) } ?: "N/A"
        } catch (e: Exception) {
            dateString ?: "N/A"
        }
    }

    fun updateItems(newItems: List<Order>) {
        this.orders = newItems
        notifyDataSetChanged()
    }
}