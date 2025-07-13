package com.alijt.foodapp.model

data class AuthResponse(
    val message: String,
    val user_id: String,
    val token: String,
    val user: User
)