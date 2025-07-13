package com.alijt.foodapp.model

data class VendorListRequest(
    val search: String? = null,
    val keywords: List<String>? = null
)