package com.example.mini_project_prm.models

data class CartItem(
    val name: String,
    val description: String,
    val priceSale: Double,
    val priceOriginal: Double,
    var quantity: Int,
    val imageResId: Int
)