package com.alijt.foodapp.repository

import com.alijt.foodapp.model.ErrorResponse
import com.alijt.foodapp.model.Restaurant
import com.alijt.foodapp.model.VendorListRequest
import com.alijt.foodapp.network.RetrofitClient
import com.google.gson.Gson

class RestaurantRepository {
    private val apiService = RetrofitClient.instance

    suspend fun getRestaurants(token: String, request: VendorListRequest): Result<List<Restaurant>> {
        return try {
            val response = apiService.getVendors(token, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).error
                } catch (e: Exception) {
                    "Failed to get restaurants: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}