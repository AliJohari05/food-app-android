package com.alijt.foodapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Restaurant(
    val id: Int? = null,
    val name: String,
    val description: String? = null,
    val address: String,
    val phone: String,
    val email: String? = null,
    val status: String? = null,
    @SerializedName("opening_time")
    val openingTime: String? = null,
    @SerializedName("closing_time")
    val closingTime: String? = null,
    @SerializedName("average_rating")
    val averageRating: Float? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerializedName("profile_image_url")
    val profileImageUrl: String? = null,
    @SerializedName("cover_image_url")
    val coverImageUrl: String? = null,
    @SerializedName("tax_fee_percentage")
    val taxFeePercentage: Float? = null,
    @SerializedName("delivery_fee")
    val deliveryFee: Float? = null,

    val approved: Boolean? = null,
    @SerializedName("logoBase64")
    val logobase64: String? = null,
    @SerializedName("tax_fee") val taxFee: Int? = null,
    @SerializedName("additional_fee") val additionalFee: Int? = null,

    val owner: Owner? = null
) : Parcelable

@Parcelize
data class Owner(
    @SerializedName("userId") val userId: Int,
    val name: String?,
    val phone: String?,
    val email: String?,
    val password: String?,
    val role: String?,
    val address: String?,
    @SerializedName("bankName") val bankName: String?,
    @SerializedName("accountNumber") val accountNumber: String?,
    val createdAt: String?,
    val updatedAt: String?,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
    val status: String?,
    val walletBalance: Double?
) : Parcelable