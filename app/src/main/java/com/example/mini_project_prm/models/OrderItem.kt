package com.example.mini_project_prm.models

import com.google.gson.annotations.SerializedName

data class OrderItem(
    @SerializedName("orderitemid")
    val id: Int? = null,

    @SerializedName("orderid")
    val orderId: Int,

    @SerializedName("figureid")
    val figureId: Int,

    @SerializedName("quantity")
    var quantity: Int,

    @SerializedName("unitprice")
    val unitPrice: Double,

    @SerializedName("imageResId")
    val imageResId: Int
)