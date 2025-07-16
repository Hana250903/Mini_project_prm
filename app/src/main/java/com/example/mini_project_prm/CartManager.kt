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

// trong file models/CartManager.kt

    // Hàm này sẽ tạo ra CartItem "phẳng" từ một đối tượng Figure
    fun addItem(figure: Figure, quantity: Int = 1) {
        // Tìm xem sản phẩm đã có trong giỏ chưa qua figure.id
        val existingItem = cartItems.find { it.figureId == figure.id }

        if (existingItem != null) {
            // Nếu có rồi thì chỉ tăng số lượng
            existingItem.quantity += quantity
        } else {
            // Nếu chưa có, tạo CartItem mới từ thông tin của Figure
            val cartItem = CartItem(
                name = figure.name,
                description = figure.description,
                priceSale = figure.price,
                priceOriginal = figure.price, // Tạm thời để giá gốc bằng giá bán
                quantity = quantity,
                imageUrl = figure.imageUrl,
                figureId = figure.id
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