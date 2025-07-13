package com.alijt.foodapp.model

import com.google.gson.annotations.SerializedName

data class Restaurant(
    val id : Int,
    val name : String,
    val address : String,
    val phone : String,
    val logoBase64 : String,
    @SerializedName("tax_fee")
    val taxFee : Int,
    @SerializedName("additional_fee")
    val additionalFee : Int

) 