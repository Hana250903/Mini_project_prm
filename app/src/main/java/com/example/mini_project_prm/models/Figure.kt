package com.example.mini_project_prm.models

import com.google.gson.annotations.SerializedName

// Không cần @Serializable khi dùng Gson
data class Figure(
    // Dùng @SerializedName nếu tên cột trong Supabase khác tên biến
    @SerializedName("figureId")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("price")
    val price: Double,

    @SerializedName("brand")
    val brand: String?,

    @SerializedName("releaseDate")
    val releaseDate: String?,

    @SerializedName("category")
    val category: String?,

    @SerializedName("series")
    val series: String?,

    @SerializedName("stock")
    val stock: Int,

    @SerializedName("imageUrl")
    val imageUrl: Int
)