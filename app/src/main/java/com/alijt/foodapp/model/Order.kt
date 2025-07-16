package com.alijt.foodapp.model

import com.google.gson.annotations.SerializedName

data class Order(
    val id: Int,
    @SerializedName("delivery_address") val deliveryAddress: String,
    @SerializedName("customer_id") val customerId: Int,
    @SerializedName("vendor_id") val vendorId: Int,
    @SerializedName("coupon_id") val couponId: Int?, // Nullable
    @SerializedName("item_ids") val itemIds: List<Int>, // List of item IDs
    @SerializedName("raw_price") val rawPrice: Int,
    @SerializedName("tax_fee") val taxFee: Int,
    @SerializedName("courier_fee") val courierFee: Int,
    @SerializedName("additional_fee") val additionalFee: Int,
    @SerializedName("pay_price") val payPrice: Int,
    @SerializedName("courier_id") val courierId: Int?, // Nullable
    val status: String, // e.g., "submitted", "waiting vendor", "completed"
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)