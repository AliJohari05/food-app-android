// FoodApp/app/src/main/java/com/alijt/foodapp/repository/AdminRepository.kt
package com.alijt.foodapp.repository

import com.alijt.foodapp.model.ErrorResponse
import com.alijt.foodapp.model.MessageResponse
import com.alijt.foodapp.model.User
import com.alijt.foodapp.model.UserStatusUpdateRequest
import com.alijt.foodapp.network.ApiService
import com.google.gson.Gson
import retrofit2.Response

class AdminRepository(private val apiService: ApiService) {

    suspend fun getAllUsers(token: String): Result<List<User>> {
        return try {
            val response = apiService.getAllUsers(token)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty users list response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).error
                } catch (e: Exception) {
                    "Failed to fetch users: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserStatus(token: String, userId: String, request: UserStatusUpdateRequest): Result<MessageResponse> {
        return try {
            val response = apiService.updateUserStatus(token, userId, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty update status response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).error
                } catch (e: Exception) {
                    "Failed to update user status: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}