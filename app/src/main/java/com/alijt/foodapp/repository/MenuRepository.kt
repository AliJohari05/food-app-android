// FoodApp/app/src/main/java/com/alijt/foodapp/repository/MenuRepository.kt
package com.alijt.foodapp.repository

import com.alijt.foodapp.model.ErrorResponse
import com.alijt.foodapp.model.RestaurantMenuDetailsResponse
import com.alijt.foodapp.network.ApiService
import com.alijt.foodapp.network.RetrofitClient
import com.google.gson.Gson
import retrofit2.Response

class MenuRepository(private val apiService: ApiService) {

    suspend fun getRestaurantMenuDetails(token: String, restaurantId: Int): Result<RestaurantMenuDetailsResponse> {
        return try {
            val response = apiService.getVendorMenuDetails(token, restaurantId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty menu details response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).error
                } catch (e: Exception) {
                    "Failed to fetch menu details: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}