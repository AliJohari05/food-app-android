package com.alijt.foodapp.model

import com.google.gson.annotations.SerializedName


data class BankInfo(
    @SerializedName("bank_name")
    val bank_name: String?,
    @SerializedName("account_number")
    val account_number: String?
)