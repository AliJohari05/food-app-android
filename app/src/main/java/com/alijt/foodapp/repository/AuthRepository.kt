package com.alijt.foodapp.repository

import com.alijt.foodapp.model.AuthResponse
import com.alijt.foodapp.model.ErrorResponse
import com.alijt.foodapp.model.LoginRequest
import com.alijt.foodapp.model.RegisterRequest
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
                    // Try to parse the error body into ErrorResponse
                    Gson().fromJson(errorBody, ErrorResponse::class.java).error
                } catch (e: Exception) {
                    // Fallback to generic error message
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
}