package com.example.mini_project_prm.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize // <-- Import dòng này

@Parcelize // <-- Thêm annotation này
data class Figure(
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

    @SerializedName("releasedate")
    val releaseDate: String?,

    @SerializedName("category")
    val category: String?,

    @SerializedName("series")
    val series: String?,

    @SerializedName("stock")
    val stock: Int,

    // imageUrl của bạn đang là Int (R.drawable.id), điều này OK
    @SerializedName("imageUrl")
    val imageUrl: String
) : Parcelable // <-- Thêm kế thừa này