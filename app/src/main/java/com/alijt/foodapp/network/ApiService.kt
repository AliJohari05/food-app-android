// FoodApp/app/src/main/java/com/alijt/foodapp/network/ApiService.kt
package com.alijt.foodapp.network

import com.alijt.foodapp.model.AuthResponse
import com.alijt.foodapp.model.LoginRequest
import com.alijt.foodapp.model.MessageResponse
import com.alijt.foodapp.model.ProfileUpdateRequest
import com.alijt.foodapp.model.RegisterRequest
import com.alijt.foodapp.model.User
import com.alijt.foodapp.model.Restaurant // Import Restaurant model
import com.alijt.foodapp.model.VendorListRequest // Import VendorListRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {

    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>

    @GET("auth/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<User>

    @PUT("auth/profile")
    suspend fun updateUserProfile(@Header("Authorization") token: String, @Body request: ProfileUpdateRequest): Response<MessageResponse>

    @POST("vendors")
    suspend fun getVendors(@Header("Authorization") token: String, @Body request: VendorListRequest): Response<List<Restaurant>>
}