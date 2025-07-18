package com.alijt.foodapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodItem(
    val id: Int? = null,
    val name: String,
    val description: String?,
    val price: Int,
    val supply: Int,
    @SerializedName("imageBase64")
    val imageBase64: String?,
    val keywords: List<String>?,
    @SerializedName("restaurant_id")
    val restaurantId: Int?,
    val categoryId: Int? = null,
    val categoryTitle: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) : Parcelable