package com.alijt.foodapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: Int? = null,
    val title: String,
    @SerializedName("restaurant_id")
    val restaurantId: Int? = null,
    val items: List<FoodItem>? = null
) : Parcelable

data class CreateMenuRequest(
    val title: String
)

data class AddItemToMenuRequest(
    @SerializedName("item_id")
    val itemId: Int
)