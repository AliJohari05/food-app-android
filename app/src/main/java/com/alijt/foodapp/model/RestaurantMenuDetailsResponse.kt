package com.alijt.foodapp.model

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement // For dynamic JSON parsing

data class RestaurantMenuDetailsResponse(
    val vendor: Restaurant, // The restaurant details
    @SerializedName("menu_titles")
    val menuTitles: List<String>, // List of menu categories like "Main Dishes", "Drinks"

    // This property captures any other top-level keys in the JSON response
    // that are not explicitly defined above. This is where your dynamic menu
    // categories (e.g., "Main Dishes", "Drinks") will be stored as JsonElement.
    // We will manually parse these JsonElements into List<FoodItem> in the Fragment.
    val additionalProperties: Map<String, JsonElement>? = null
)