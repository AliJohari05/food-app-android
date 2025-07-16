package com.alijt.foodapp.repository

import com.alijt.foodapp.model.ErrorResponse
import com.alijt.foodapp.model.RestaurantMenuDetailsResponse
import com.alijt.foodapp.network.ApiService
import com.alijt.foodapp.model.Result
import com.google.gson.Gson
import retrofit2.Response

class MenuRepository(private val apiService: ApiService) {

    private suspend fun <T> safeApiCall(call: suspend () -> Response<T>, defaultErrorMessage: String): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it) // استفاده صحیح از Result.Success
                } ?: Result.Failure(Exception("Empty response body for successful call: $defaultErrorMessage")) // استفاده صحیح از Result.Failure
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).error
                } catch (e: Exception) {
                    "$defaultErrorMessage (HTTP ${response.code()})"
                }
                Result.Failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure(e)
        }
    }

    suspend fun getRestaurantMenuDetails(token: String, restaurantId: Int): Result<RestaurantMenuDetailsResponse> {
        return safeApiCall(
            call = { apiService.getVendorMenuDetails("Bearer $token", restaurantId.toString()) }, // restaurantId.toString() اضافه شد
            defaultErrorMessage = "Failed to fetch menu details"
        )
    }
}