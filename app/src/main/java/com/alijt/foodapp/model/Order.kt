package com.alijt.foodapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: Int,
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("coupon_id")
    val couponId: Int?,
    @SerializedName("item_ids")
    val itemIds: List<Int>,
    @SerializedName("raw_price")
    val rawPrice: Int,
    @SerializedName("tax_fee")
    val taxFee: Int,
    @SerializedName("additional_fee")
    val additionalFee: Int,
    @SerializedName("courier_fee")
    val courierFee: Int,
    @SerializedName("pay_price")
    val payPrice: Int,
    @SerializedName("courier_id")
    val courierId: Int?,
    val status: String, // submitted, waiting vendor, accepted, rejected, served, finding courier, on the way, completed, cancelled, unpaid
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
) : Parcelable

data class OrderStatusUpdateRequest(
    val status: String // accepted, rejected, served
)