package com.example.mini_project_prm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project_prm.fragments.CartFragment
import com.example.mini_project_prm.R
import com.example.mini_project_prm.models.CartItem
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val cartItems: MutableList<CartItem>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

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

        holder.imgProduct.setImageResource(item.imageUrl)
        holder.tvName.text = item.name
        holder.tvDescription.text = item.description

        val formattedPriceSale = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(item.priceSale)
        holder.tvPriceSale.text = "${formattedPriceSale}đ"
        val formattedPriceOriginal = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(item.priceOriginal)
        holder.tvPriceOriginal.text = "${formattedPriceOriginal}đ"

        holder.tvQuantity.text = item.quantity.toString()

        holder.btnIncrease.setOnClickListener {
            item.quantity++
            notifyItemChanged(position)

            // Gọi updateTotalPrice trong CartFragment
            (holder.itemView.context as? FragmentActivity)?.supportFragmentManager
                ?.findFragmentById(R.id.fragmentContainer)?.let {
                    if (it is CartFragment) {
                        it.updateTotalPrice()
                    }
                }
        }

        holder.btnDecrease.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
                notifyItemChanged(position)
                // Gọi updateTotalPrice
                (holder.itemView.context as? FragmentActivity)?.supportFragmentManager
                    ?.findFragmentById(R.id.fragmentContainer)?.let {
                        if (it is CartFragment) {
                            it.updateTotalPrice()
                        }
                    }
            }
        }

        holder.tvDelete.setOnClickListener {
            cartItems.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cartItems.size)
            // Gọi updateTotalPrice
            (holder.itemView.context as? FragmentActivity)?.supportFragmentManager
                ?.findFragmentById(R.id.fragmentContainer)?.let {
                    if (it is CartFragment) {
                        it.updateTotalPrice()
                    }
                }
        }
    }

    override fun getItemCount(): Int = cartItems.size
}
