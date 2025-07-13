package com.alijt.foodapp.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    val phone: String,
    val role: String,
    @SerializedName("full_name")
    val fullName: String?, // Make it nullable if it can be null from backend
    val email: String?,
    val address: String?,
    @SerializedName("profileImageUrl") // If your backend sends a URL for the image
    val profileImageUrl: String?,
    @SerializedName("bank_info")
    val bank_info: BankInfo?
)
