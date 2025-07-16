package com.example.mini_project_prm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mini_project_prm.R
import com.example.mini_project_prm.models.CartItem
import com.example.mini_project_prm.models.CartManager // Import CartManager
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onCartUpdated: () -> Unit // Listener đã rất tốt
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // ViewHolder của bạn đã đúng, không cần sửa
    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduct: ImageView = view.findViewById(R.id.imgProduct)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvPriceSale: TextView = view.findViewById(R.id.tvPriceSale)
        val tvPriceOriginal: TextView = view.findViewById(R.id.tvPriceOriginal)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val btnDecrease: Button = view.findViewById(R.id.btnDecrease)
        val btnIncrease: Button = view.findViewById(R.id.btnIncrease)
        val tvDelete: TextView = view.findViewById(R.id.tvDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]

        // Phần hiển thị dữ liệu đã đúng, không cần sửa
        holder.imgProduct.load(item.imageUrl) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_report_image)
            error(android.R.drawable.stat_notify_error)
        }
        holder.tvName.text = item.name
        holder.tvDescription.text = item.description
        val formattedPriceSale = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(item.priceSale)
        holder.tvPriceSale.text = "${formattedPriceSale}đ"
        val formattedPriceOriginal = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(item.priceOriginal)
        holder.tvPriceOriginal.text = "${formattedPriceOriginal}đ"
        holder.tvQuantity.text = item.quantity.toString()

        // === PHẦN ĐÃ ĐƯỢC DỌN DẸP ===
        holder.btnIncrease.setOnClickListener {
            item.quantity++
            notifyItemChanged(position)
            onCartUpdated() // Chỉ cần gọi listener là đủ
        }

        holder.btnDecrease.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
                notifyItemChanged(position)
                onCartUpdated() // Chỉ cần gọi listener là đủ
            }
        }

        holder.tvDelete.setOnClickListener {
            // Bước 1: Yêu cầu CartManager xóa item
            CartManager.removeItem(item)
            // Bước 2: Báo cho Fragment cập nhật lại toàn bộ UI
            onCartUpdated()
        }
    }

    override fun getItemCount(): Int = cartItems.size
}