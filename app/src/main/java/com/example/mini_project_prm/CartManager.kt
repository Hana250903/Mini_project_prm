package com.example.mini_project_prm.models

import android.widget.Toast
import com.example.mini_project_prm.R

// Dùng 'object' để tạo ra một Singleton
object CartManager {

    private val cartItems = mutableListOf<CartItem>()

    // Hàm để lấy danh sách các sản phẩm trong giỏ
    fun getCartItems(): MutableList<CartItem> {
        return cartItems
    }

    // Hàm để thêm sản phẩm vào giỏ
    fun addItem(figure: Figure, quantity: Int = 1) {
        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        val existingItem = cartItems.find { it.figureId == figure.id }

        if (existingItem != null) {
            // Nếu có rồi thì chỉ tăng số lượng
            existingItem.quantity += quantity
        } else {
            // Nếu chưa có thì tạo CartItem mới và thêm vào danh sách
            val cartItem = CartItem(
                figureId = figure.id,
                name = figure.name,
                description = figure.description,
                priceSale = figure.price, // Giả sử giá bán bằng giá gốc
                priceOriginal = figure.price * 1.1, // Giả sử giá gốc cao hơn 10%
                imageUrl = figure.imageUrl,
                quantity = quantity
            )
            cartItems.add(cartItem)
        }
    }

    fun removeItem(cartItem: CartItem) {
        cartItems.remove(cartItem)
    }
    // Các hàm khác như xóa sản phẩm, tính tổng tiền có thể thêm ở đây
    fun getTotalPrice(): Double {
        return cartItems.sumOf { it.priceSale * it.quantity }
    }

    fun clearCart() {
        cartItems.clear()
    }


}