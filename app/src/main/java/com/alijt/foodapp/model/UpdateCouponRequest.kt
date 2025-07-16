package com.alijt.foodapp.model

data class UpdateCouponRequest(
    val coupon_code: String?,
    val type: String?,
    val value: Double?,
    val min_price: Int?,
    val user_count: Int?,
    val start_date: String?,
    val end_date: String?
)