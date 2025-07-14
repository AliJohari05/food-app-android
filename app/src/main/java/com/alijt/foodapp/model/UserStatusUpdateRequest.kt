package com.alijt.foodapp.model

data class UserStatusUpdateRequest(
    val status: String // Can be "approved" or "rejected"
)