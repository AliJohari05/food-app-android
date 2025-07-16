package com.alijt.foodapp.model

data class Transaction(
    val id: Int,
    val order_id: Int?, // Can be null if it's a wallet top-up, not directly related to an order
    val user_id: Int,
    val method: String, // wallet or online
    val status: String, // success or failed
    val amount: Double // Assuming amount is part of transaction, added based on common sense
)