package com.alijt.foodapp.network

import com.alijt.foodapp.model.AuthResponse
import com.alijt.foodapp.model.LoginRequest
import com.alijt.foodapp.model.MessageResponse
import com.alijt.foodapp.model.ProfileUpdateRequest
import com.alijt.foodapp.model.RegisterRequest
import com.alijt.foodapp.model.Restaurant
import com.alijt.foodapp.model.User
import com.alijt.foodapp.model.UserStatusUpdateRequest // Import UserStatusUpdateRequest
import com.alijt.foodapp.model.VendorListRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH // Import PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path // Import Path

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

    // Admin APIs for User Management
    @GET("admin/users")
    suspend fun getAllUsers(@Header("Authorization") token: String): Response<List<User>>

    @PATCH("admin/users/{id}/status")
    suspend fun updateUserStatus(
        @Header("Authorization") token: String,
        @Path("id") userId: String, // User ID from path
        @Body request: UserStatusUpdateRequest
    ): Response<MessageResponse>
}