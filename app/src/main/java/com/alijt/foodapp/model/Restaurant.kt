package com.alijt.foodapp.model

import com.google.gson.annotations.SerializedName

data class Restaurant(
    val id: Int,
    val name: String,
    val description: String?,
    val address: String,
    val phone: String,
    val email: String?,
    val status: String?, // CHANGED: Made status nullable to match actual API response
    @SerializedName("opening_time")
    val openingTime: String?, // Made nullable, check if backend always sends
    @SerializedName("closing_time")
    val closingTime: String?, // Made nullable, check if backend always sends
    @SerializedName("average_rating")
    val averageRating: Float?,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("profile_image_url")
    val profileImageUrl: String?,
    @SerializedName("cover_image_url")
    val coverImageUrl: String?,
    @SerializedName("tax_fee_percentage")
    val taxFeePercentage: Float?,
    @SerializedName("delivery_fee")
    val deliveryFee: Float?,

    // If "approved" is always sent, you might want to add it explicitly
    val approved: Boolean? = null, // Added based on the actual JSON response
    // The actual JSON also has logobase64, taxFee, additionalFee which contradict YAML
    // You have to decide if you follow YAML strictly or actual backend response.
    // Given your original Restaurant.kt had these, I'm adding them back as nullable for now
    val logobase64: String? = null,
    @SerializedName("taxFee") val taxFee: Int? = null, // Based on JSON, but YAML has tax_fee_percentage
    @SerializedName("additionalFee") val additionalFee: Int? = null, // Based on JSON, but YAML has delivery_fee

    // If the 'owner' object is needed:
    val owner: Owner? = null // You might need to define an Owner data class if you use this nested data
)
// You might need an Owner data class if you want to access nested fields like owner.status
data class Owner(
    @SerializedName("userId") val userId: Int,
    val name: String?,
    val phone: String?,
    val email: String?,
    val password: String?, // Sensitive, usually not sent
    val role: String?,
    val address: String?,
    @SerializedName("bankName") val bankName: String?,
    @SerializedName("accountNumber") val accountNumber: String?,
    val createdAt: String?,
    val updatedAt: String?,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
    val status: String?, // The status that caused NPE in log
    @SerializedName("walletBalance") val walletBalance: Double?
)