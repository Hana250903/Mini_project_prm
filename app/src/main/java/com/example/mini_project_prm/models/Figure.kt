package com.example.mini_project_prm.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Figure(
    // Sửa lại cho khớp với JSON: "figureid"
    @SerializedName("figureid")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("price")
    val price: Double,

    @SerializedName("brand")
    val brand: String?,

    // Sửa lại cho khớp với JSON: "releasedate"
    @SerializedName("releasedate")
    val releaseDate: String?,

    @SerializedName("category")
    val category: String?,

    @SerializedName("series")
    val series: String?,

    @SerializedName("stock")
    val stock: Int,

    // Sửa lại cho khớp với JSON: "imageurl" và kiểu dữ liệu là String
    @SerializedName("imageurl")
    val imageUrl: String

) : Parcelable