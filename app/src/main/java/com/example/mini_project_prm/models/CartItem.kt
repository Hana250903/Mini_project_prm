package com.example.mini_project_prm.models

data class CartItem(
    val figureId: Int,
    val name: String,
    val description: String?,
    val priceSale: Double,
    val priceOriginal: Double,
    val imageUrl: Int,
    var quantity: Int
)