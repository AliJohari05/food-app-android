package com.alijt.foodapp.network

import com.alijt.foodapp.model.AuthResponse
import com.alijt.foodapp.model.LoginRequest
import com.alijt.foodapp.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>
}