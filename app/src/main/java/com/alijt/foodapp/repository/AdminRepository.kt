package com.alijt.foodapp.repository

import com.alijt.foodapp.model.*
import com.alijt.foodapp.network.ApiService
import com.google.gson.Gson
import retrofit2.Response

class AdminRepository(private val apiService: ApiService) {

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

    suspend fun getAllUsers(token: String): Result<List<User>> {
        return safeApiCall(
            call = { apiService.getAllUsers("Bearer $token") },
            defaultErrorMessage = "Failed to fetch users"
        )
    }

    suspend fun updateUserStatus(token: String, userId: String, request: UserStatusUpdateRequest): Result<String> {
        return safeApiCall(
            call = { apiService.updateUserStatus(userId, request, "Bearer $token") },
            defaultErrorMessage = "Failed to update user status"
        )
    }

    suspend fun getAllOrders(
        token: String,
        status: String? = null,
        search: String? = null,
        vendor: String? = null,
        courier: String? = null,
        customer: String? = null
    ): Result<List<Order>> {
        return safeApiCall(
            call = { apiService.getAllOrders("Bearer $token", status, search, vendor, courier, customer) },
            defaultErrorMessage = "Failed to fetch orders"
        )
    }

    suspend fun getAllTransactions(
        token: String,
        search: String? = null,
        user: String? = null,
        method: String? = null,
        status: String? = null
    ): Result<List<Transaction>> {
        return safeApiCall(
            call = { apiService.getAllTransactions("Bearer $token", search, user, method, status) },
            defaultErrorMessage = "Failed to fetch transactions"
        )
    }

    suspend fun getAllCoupons(token: String): Result<List<Coupon>> {
        return safeApiCall(
            call = { apiService.getAllCoupons("Bearer $token") },
            defaultErrorMessage = "Failed to fetch coupons"
        )
    }

    suspend fun createCoupon(token: String, request: CreateCouponRequest): Result<Coupon> {
        return safeApiCall(
            call = { apiService.createCoupon(request, "Bearer $token") },
            defaultErrorMessage = "Failed to create coupon"
        )
    }

    suspend fun getCouponDetails(token: String, couponId: String): Result<Coupon> {
        return safeApiCall(
            call = { apiService.getCouponDetails(couponId, "Bearer $token") },
            defaultErrorMessage = "Failed to get coupon details"
        )
    }

    suspend fun updateCoupon(token: String, couponId: String, request: UpdateCouponRequest): Result<Coupon> {
        return safeApiCall(
            call = { apiService.updateCoupon(couponId, request, "Bearer $token") },
            defaultErrorMessage = "Failed to update coupon"
        )
    }

    suspend fun deleteCoupon(token: String, couponId: String): Result<String> {
        return safeApiCall(
            call = { apiService.deleteCoupon(couponId, "Bearer $token") },
            defaultErrorMessage = "Failed to delete coupon"
        )
    }
}