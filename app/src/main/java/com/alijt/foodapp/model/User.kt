package com.alijt.foodapp.model

data class User(
    val id: String,
    val phone: String,
    val email: String?,
    val address: String,
    val role: String,
    val fullName: String,
    val profileImageUrl: String? = null,
    val status: String? = null,
    val bank_info: BankInfo? = null,
    val wallet_balance: Double? = null,
    val profileImageBase64: String? = null
)