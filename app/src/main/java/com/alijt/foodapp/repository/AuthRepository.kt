package com.alijt.foodapp.repository

import com.alijt.foodapp.model.AuthResponse
import com.alijt.foodapp.model.ErrorResponse
import com.alijt.foodapp.model.LoginRequest
import com.alijt.foodapp.model.RegisterRequest
import com.alijt.foodapp.model.User // Import User model
import com.alijt.foodapp.model.MessageResponse // Import MessageResponse
import com.alijt.foodapp.model.ProfileUpdateRequest // Import ProfileUpdateRequest
import com.alijt.foodapp.network.RetrofitClient
import com.google.gson.Gson
import retrofit2.Response

class AuthRepository {

    private val apiService = RetrofitClient.instance

    suspend fun registerUser(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = apiService.registerUser(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).error
                } catch (e: Exception) {
                    "Registration failed: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(request: LoginRequest): Result<AuthResponse>{
        return try {
            val response = apiService.loginUser(request)
            if(response.isSuccessful){
                response.body()?.let{
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            }else{
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody,ErrorResponse::class.java).error
                }catch (e:Exception){
                    "Login failed: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        }catch (e:Exception){
            Result.failure(e)
        }
    }


    suspend fun fetchUserProfile(token: String): Result<User> {
        return try {
            val response = apiService.getUserProfile(token)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty user profile response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).error
                } catch (e: Exception) {
                    "Failed to fetch profile: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(token: String, request: ProfileUpdateRequest): Result<MessageResponse> {
        return try {
            val response = apiService.updateUserProfile(token, request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty update profile response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).error
                } catch (e: Exception) {
                    "Failed to update profile: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}