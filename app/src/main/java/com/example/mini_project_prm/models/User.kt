package com.example.mini_project_prm.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userId")
    val id: Int,

    @SerializedName("fullName")
    val fullName: String?,

    @SerializedName("email")
    val email: String,

    // Password hash thường không nên lấy về client, nhưng vẫn khai báo nếu cần
    @SerializedName("passwordHash")
    val passwordHash: String?,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("role")
    val role: String,

    @SerializedName("createdAt")
    val createdAt: String // Dữ liệu ngày tháng từ API
)