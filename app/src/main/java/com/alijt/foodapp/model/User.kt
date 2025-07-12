package com.alijt.foodapp.model

data class User(
    val id: String,
    val full_name: String,
    val phone: String,
    val email: String?,
    val role: String,
    val address: String
)