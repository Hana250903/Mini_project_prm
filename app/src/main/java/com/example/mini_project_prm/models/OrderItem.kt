package com.example.mini_project_prm.models

import com.google.gson.annotations.SerializedName

data class OrderItem(
    @SerializedName("orderItemId")
    val id: Int,

    @SerializedName("orderId")
    val orderId: Int,

    @SerializedName("figureId")
    val figureId: Int,

    @SerializedName("quantity")
    var quantity: Int,

    @SerializedName("unitPrice")
    val unitPrice: Double,

    @SerializedName("imageResId")
    val imageResId: Int
)