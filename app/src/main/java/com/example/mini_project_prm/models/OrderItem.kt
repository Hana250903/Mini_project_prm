package com.example.mini_project_prm.models

import com.google.gson.annotations.SerializedName

data class OrderItem(
    // Giả sử khóa chính của bảng orderitems là 'id'
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("orderid")
    val orderId: Int,

    @SerializedName("figureid")
    val figureId: Int,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("unitprice")
    val unitPrice: Double,

    // Trường này sẽ được Supabase điền vào khi bạn dùng select=*,figures(*)
    // Nó có thể là null, đặc biệt là khi bạn tạo một OrderItem mới để gửi đi
    @SerializedName("figures")
    val figure: Figure? = null
)