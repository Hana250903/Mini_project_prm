package com.example.mini_project_prm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mini_project_prm.R
import com.example.mini_project_prm.models.OrderItem
import java.text.NumberFormat
import java.util.Locale

class OrderItemAdapter(private val items: List<OrderItem>) :
    RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    inner class OrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProduct: ImageView = view.findViewById(R.id.ivProductImage)
        val tvName: TextView = view.findViewById(R.id.tvProductName)
        val tvPrice: TextView = view.findViewById(R.id.tvProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = items[position]
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

        // Sử dụng dữ liệu từ đối tượng figure lồng bên trong
        // Dùng let để xử lý an toàn trường hợp figure có thể là null
        item.figure?.let { figure ->
            // Hiển thị tên sản phẩm và số lượng
            holder.tvName.text = "${figure.name} (SL: ${item.quantity})"
            // Tải ảnh sản phẩm từ URL
            holder.ivProduct.load(figure.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background) // Ảnh tạm
                error(R.drawable.ic_launcher_foreground) // Ảnh lỗi
            }
        } ?: run {
            // Trường hợp không có dữ liệu figure (dự phòng)
            // Điều này không nên xảy ra nếu bạn đã thiết lập Foreign Key đúng
            holder.tvName.text = "Sản phẩm ID: ${item.figureId} (SL: ${item.quantity})"
            holder.ivProduct.setImageResource(R.drawable.ic_launcher_foreground) // Ảnh lỗi
        }

        // Hiển thị giá lúc mua
        holder.tvPrice.text = formatter.format(item.unitPrice)
    }

    override fun getItemCount(): Int = items.size
}