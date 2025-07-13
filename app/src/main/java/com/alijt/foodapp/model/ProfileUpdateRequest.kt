package com.alijt.foodapp.model

import com.google.gson.annotations.SerializedName
import com.alijt.foodapp.model.BankInfo

data class ProfileUpdateRequest(
    @SerializedName("full_name") val fullName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val profileImageBase64: String? = null, // For image upload
    val bank_info: BankInfo? = null // For bank details update
)