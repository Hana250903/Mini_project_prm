package com.example.mini_project_prm.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userid")
    val id: Int? =null,

    @SerializedName("fullname")
    val fullName: String?,

    @SerializedName("email")
    val email: String,

    // Password hash thường không nên lấy về client, nhưng vẫn khai báo nếu cần
    @SerializedName("passwordhash")
    val passwordHash: String?,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("role")
    val role: String,

    @SerializedName("gender")
    val gender: String?,

    @SerializedName("dob")
    val dob: String?,

    @SerializedName("countries")
    val countries: String?,

    @SerializedName("city")
    val  city: String?,

    @SerializedName("createdat")
    val createdAt: String? = null // Dữ liệu ngày tháng từ API
)