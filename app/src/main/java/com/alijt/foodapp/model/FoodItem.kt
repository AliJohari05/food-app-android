// FoodApp/app/src/main/java/com/alijt/foodapp/model/FoodItem.kt
package com.alijt.foodapp.model

import com.google.gson.annotations.SerializedName

data class FoodItem(
    val id: Int,
    val name: String,
    @SerializedName("imageBase64") // Based on backend DTO's name for image field
    val imageUrl: String?, // Changed name from imageBase64 to imageUrl as it holds a URL
    val description: String?,
    val price: Double, // CHANGED: From Int to Double to match BigDecimal/Decimal from backend
    val supply: Int,
    val keywords: String?, // CHANGED: From List<String>? to String? to match backend
    val categories: List<String>?
)