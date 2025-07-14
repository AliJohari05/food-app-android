package com.alijt.foodapp.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: String, // As String to match potential backend type
    @SerializedName("full_name") val fullName: String, // Ensure matches backend (snake_case in YAML)
    val phone: String,
    val email: String?,
    val role: String,
    val address: String?,
    @SerializedName("profileImageBase64") val profileImageBase64: String?, // For images
    @SerializedName("profileImageUrl") val profileImageUrl: String?, // Also exists in Restaurant owner, but might be here too
    val bank_info: BankInfo?,
    val status: String?, // For user status (APPROVED, REJECTED, etc.)
    @SerializedName("wallet_balance") val walletBalance: Double?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
