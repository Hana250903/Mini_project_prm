package com.example.mini_project_prm.adapters

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

class FigureAdapter(private val figures: List<Figure>):
    RecyclerView.Adapter<FigureAdapter.FigureViewHolder>() {
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
    }

    override fun getItemCount(): Int = figures.size
}