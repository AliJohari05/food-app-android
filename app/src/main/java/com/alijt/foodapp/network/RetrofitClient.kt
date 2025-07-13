// FoodApp/app/src/main/java/com/alijt/foodapp/network/RetrofitClient.kt
package com.alijt.foodapp.network

import android.content.Context // Import Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.alijt.foodapp.utils.SessionManager // Import SessionManager

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Lazy initialization of SessionManager to ensure context is available
    private lateinit var applicationContext: Context
    private val sessionManager: SessionManager by lazy {
        SessionManager(applicationContext)
    }

    // Function to initialize the RetrofitClient with application context
    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    // Create a logging interceptor for debugging network requests
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Log request and response bodies
    }

    // Create an authentication interceptor to add the Bearer token
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Get the token from SessionManager
        val token = sessionManager.getAuthToken()

        // If token exists, add it to the Authorization header with "Bearer " prefix
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        chain.proceed(requestBuilder.build())
    }

    // Create a custom interceptor to ensure Content-Type header is always set
    private val contentTypeInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        if ((originalRequest.method == "POST" || originalRequest.method == "PUT") &&
            originalRequest.header("Content-Type") == null) {
            requestBuilder.header("Content-Type", "application/json")
        }

        chain.proceed(requestBuilder.build())
    }

    // Build the OkHttpClient with all interceptors
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor) // Add auth interceptor first to add token
        .addInterceptor(contentTypeInterceptor) // Then Content-Type
        .addInterceptor(loggingInterceptor) // Then logging
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        // Ensure applicationContext is initialized before accessing SessionManager
        if (!::applicationContext.isInitialized) {
            throw IllegalStateException("RetrofitClient must be initialized with context via RetrofitClient.init(context) before use.")
        }
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}