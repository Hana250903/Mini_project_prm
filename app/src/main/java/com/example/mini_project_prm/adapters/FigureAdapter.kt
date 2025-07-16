package com.example.mini_project_prm.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project_prm.models.Figure
import com.example.mini_project_prm.R
import java.text.NumberFormat
import java.util.Locale
import androidx.appcompat.app.AppCompatActivity
import com.example.mini_project_prm.fragments.ProductDetailFragment

class FigureAdapter(
    private var figures: List<Figure> // Đổi private val thành private var
) : RecyclerView.Adapter<FigureAdapter.FigureViewHolder>() {
    inner class FigureViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imgFigure: ImageView = itemView.findViewById(R.id.imgFigure)
        val textName: TextView = itemView.findViewById(R.id.txtName)
        val price: TextView = itemView.findViewById(R.id.txtPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FigureViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_figure, parent, false)
        return FigureViewHolder(view)
    }

    override fun onBindViewHolder(holder: FigureViewHolder, position: Int) {
        val figure = figures[position]
        holder.imgFigure.setImageResource(figure.imageUrl)
        holder.textName.text = figure.name
        val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(figure.price)
        holder.price.text = "${formattedPrice}đ"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            // Kiểm tra context có phải là một Activity không
            if (context is AppCompatActivity) {
                // Tạo một instance của Fragment mới
                val detailFragment = ProductDetailFragment.newInstance(figure)

                // Thực hiện việc thay thế Fragment
                context.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, detailFragment)
                    .addToBackStack(null) // <-- Rất quan trọng để nút back hoạt động
                    .commit()
            }
        }

    }

    fun updateData(newFigures: List<Figure>) {
        this.figures = newFigures
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = figures.size
}