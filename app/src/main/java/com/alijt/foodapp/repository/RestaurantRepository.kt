package com.alijt.foodapp.repository

import com.alijt.foodapp.model.ErrorResponse
import com.alijt.foodapp.model.Restaurant
import com.alijt.foodapp.model.VendorListRequest
import com.alijt.foodapp.network.ApiService
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.utils.SessionManager
import com.google.gson.Gson
import retrofit2.Response

class RestaurantRepository(private val apiService: ApiService, private val sessionManager: SessionManager) {

    private suspend inline fun <reified T> safeApiCall(call: suspend () -> Response<T>, defaultErrorMessage: String): Result<T> { // <-- اینجا اصلاح شد
        return try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: run {
                    if (T::class.java == com.alijt.foodapp.model.MessageResponse::class.java) {
                        val messageString = response.errorBody()?.string() ?: "Operation successful"
                        @Suppress("UNCHECKED_CAST")
                        Result.Success(com.alijt.foodapp.model.MessageResponse(message = messageString)) as Result<T>
                    } else {
                        Result.Failure(Exception("Empty response body for successful call: $defaultErrorMessage"))
                    }
                }
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

    suspend fun fetchRestaurants(token: String, request: VendorListRequest): Result<List<Restaurant>> {
        val authToken = sessionManager.getAuthToken()
        return if (authToken != null) {
            safeApiCall(
                call = { apiService.getVendors("Bearer $authToken", request) },
                defaultErrorMessage = "Failed to fetch restaurants"
            )
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }

}