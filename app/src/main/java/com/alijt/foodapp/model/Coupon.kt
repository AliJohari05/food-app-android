package com.alijt.foodapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coupon(
    val id: Int,
    val coupon_code: String,
    val type: String, // fixed or percent
    val value: Double,
    val min_price: Int,
    val user_count: Int,
    val start_date: String, // format: YYYY-MM-DD
    val end_date: String // format: YYYY-MM-DD
) : Parcelable