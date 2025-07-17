package com.alijt.foodapp.repository

import com.alijt.foodapp.model.ErrorResponse
import com.alijt.foodapp.model.Restaurant
import com.alijt.foodapp.model.MessageResponse
import com.alijt.foodapp.network.ApiService
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.utils.SessionManager
import com.google.gson.Gson
import retrofit2.Response

class SellerRepository(private val apiService: ApiService, private val sessionManager: SessionManager) {

    private suspend inline fun <reified T> safeApiCall(call: suspend () -> Response<T>, defaultErrorMessage: String): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: run {
                    if (MessageResponse::class.java == T::class.java) {
                        val messageString = response.errorBody()?.string() ?: "Operation successful"
                        @Suppress("UNCHECKED_CAST")
                        Result.Success(MessageResponse(message = messageString)) as Result<T>
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

    suspend fun getMyRestaurants(): Result<List<Restaurant>> {
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            safeApiCall(
                call = { apiService.getMyRestaurants("Bearer $token") },
                defaultErrorMessage = "Failed to fetch seller's restaurants"
            )
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }

    suspend fun createRestaurant(restaurant: Restaurant): Result<Restaurant> {
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            safeApiCall(
                call = { apiService.createRestaurant("Bearer $token", restaurant) },
                defaultErrorMessage = "Failed to create restaurant"
            )
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }

    suspend fun updateRestaurant(restaurant: Restaurant): Result<Restaurant> {
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            restaurant.id?.let { restaurantId ->
                safeApiCall(
                    call = { apiService.updateRestaurantDetails("Bearer $token", restaurantId, restaurant) },
                    defaultErrorMessage = "Failed to update restaurant"
                )
            } ?: Result.Failure(Exception("Restaurant ID is missing for update."))
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }


}