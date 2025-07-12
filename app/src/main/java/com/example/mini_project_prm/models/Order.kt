package com.example.mini_project_prm.models

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("orderid")
    val id: Int? = null,

    @SerializedName("userid")
    val userId: Int,

    @SerializedName("orderdate")
    val orderDate: String,

    @SerializedName("totalamount")
    val totalAmount: Double,

    @SerializedName("status")
    val status: String
)