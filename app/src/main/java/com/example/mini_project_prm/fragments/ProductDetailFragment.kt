package com.example.mini_project_prm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.mini_project_prm.R
import com.example.mini_project_prm.models.CartManager
import com.example.mini_project_prm.models.Figure
import java.text.NumberFormat
import java.util.Locale

class ProductDetailFragment : Fragment() {

    // Companion object để tạo fragment với tham số một cách an toàn
    companion object {
        private const val ARG_FIGURE = "ARG_FIGURE"

        fun newInstance(figure: Figure): ProductDetailFragment {
            val fragment = ProductDetailFragment()
            val args = Bundle()
            args.putParcelable(ARG_FIGURE, figure)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy dữ liệu Figure từ arguments
        val figure: Figure? = arguments?.getParcelable(ARG_FIGURE)

        if (figure == null) {
            // Nếu không có dữ liệu, quay lại
            activity?.onBackPressedDispatcher?.onBackPressed()
            return
        }

        // Xử lý nút back trên toolbar
        val toolbar: Toolbar = view.findViewById(R.id.toolbarDetail)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        // Ánh xạ View và hiển thị dữ liệu (giống như trong Activity cũ)
        val ivProductImage: ImageView = view.findViewById(R.id.ivProductImageDetail)
        val tvProductName: TextView = view.findViewById(R.id.tvProductNameDetail)
        val tvProductPrice: TextView = view.findViewById(R.id.tvProductPriceDetail)
        val tvProductDescription: TextView = view.findViewById(R.id.tvProductDescription)

        // Hiển thị dữ liệu lên UI
        ivProductImage.setImageResource(figure.imageUrl)
        tvProductName.text = figure.name
        val formattedPrice = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(figure.price)
        tvProductPrice.text = "${formattedPrice}đ"
        tvProductDescription.text = figure.description ?: "Không có mô tả cho sản phẩm này."

        // Xử lý logic số lượng và thêm vào giỏ hàng (giống như trong Activity cũ)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantityDetail)
        val btnDecrease: Button = view.findViewById(R.id.btnDecreaseQuantity)
        val btnIncrease: Button = view.findViewById(R.id.btnIncreaseQuantity)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCartDetail)
        var currentQuantity = 1

        btnIncrease.setOnClickListener {
            currentQuantity++
            tvQuantity.text = currentQuantity.toString()
        }
        btnDecrease.setOnClickListener {
            if (currentQuantity > 1) {
                currentQuantity--
                tvQuantity.text = currentQuantity.toString()
            }
        }
        btnAddToCart.setOnClickListener {
            CartManager.addItem(figure, currentQuantity)
            Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show()
        }
    }
}