package com.alijt.foodapp.model

data class RegisterRequest(
    val full_name: String,
    val phone: String,
    val password: String,
    val role: String,
    val address: String,
    val email: String? = null // Optional field
)