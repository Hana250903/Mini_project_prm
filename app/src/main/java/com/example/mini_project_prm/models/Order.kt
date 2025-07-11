package com.example.mini_project_prm.models

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("orderId")
    val id: Int,

    @SerializedName("userId")
    val userId: Int,

    @SerializedName("orderDate")
    val orderDate: String,

    @SerializedName("totalAmount")
    val totalAmount: Double,

    @SerializedName("status")
    val status: String
)