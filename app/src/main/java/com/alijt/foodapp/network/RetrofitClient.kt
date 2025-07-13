// FoodApp/app/src/main/java/com/alijt/foodapp/network/RetrofitClient.kt
package com.alijt.foodapp.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // Make sure this import is present
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Create a logging interceptor for debugging network requests
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Log request and response bodies
    }

    // Create a custom interceptor to ensure Content-Type header is always set
    private val contentTypeInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Accessing method using .method (should be public in current OkHttp versions)
        // Check if Content-Type is already set and it's a POST/PUT request
        if ((originalRequest.method == "POST" || originalRequest.method == "PUT") &&
            originalRequest.header("Content-Type") == null) {
            requestBuilder.header("Content-Type", "application/json")
        }

        chain.proceed(requestBuilder.build())
    }

    // Build the OkHttpClient with the interceptors
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(contentTypeInterceptor) // Add custom Content-Type interceptor first
        .addInterceptor(loggingInterceptor) // Add logging interceptor for debugging
        .connectTimeout(30, TimeUnit.SECONDS) // Connection timeout
        .readTimeout(30, TimeUnit.SECONDS)    // Read timeout
        .writeTimeout(30, TimeUnit.SECONDS)   // Write timeout
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Set the custom OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}