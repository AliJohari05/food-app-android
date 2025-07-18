package com.alijt.foodapp.network

import com.alijt.foodapp.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Auth APIs
    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>

    @GET("auth/profile")
    suspend fun fetchUserProfile(@Header("Authorization") token: String): Response<User>

    @PUT("auth/profile")
    suspend fun updateUserProfile(@Header("Authorization") token: String, @Body request: ProfileUpdateRequest): Response<MessageResponse>

    @POST("auth/logout")
    suspend fun logoutUser(@Header("Authorization") token: String): Response<MessageResponse>

    // Restaurant APIs (Seller side)
    @POST("restaurants")
    suspend fun createRestaurant(@Header("Authorization") token: String, @Body request: Restaurant): Response<Restaurant>

    @GET("restaurants/mine")
    suspend fun getMyRestaurants(@Header("Authorization") token: String): Response<List<Restaurant>>

    @PUT("restaurants/{id}")
    suspend fun updateRestaurantDetails(@Header("Authorization") token: String, @Path("id") restaurantId: Int, @Body request: Restaurant): Response<Restaurant>

    @POST("restaurants/{id}/item")
    suspend fun addFoodItem(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Body item: FoodItem
    ): Response<FoodItem>

    @PUT("restaurants/{id}/item/{item_id}")
    suspend fun editFoodItem(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int, // id رستوران
        @Path("item_id") itemId: Int, // id آیتم غذایی
        @Body item: FoodItem // FoodItem برای به‌روزرسانی
    ): Response<FoodItem> // بازگشت FoodItem به‌روز شده

    @DELETE("restaurants/{id}/item/{item_id}")
    suspend fun deleteFoodItem(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Path("item_id") itemId: Int
    ): Response<MessageResponse> // بازگشت پیام موفقیت

    @POST("restaurants/{id}/menu")
    suspend fun createRestaurantMenu(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Body createMenuRequest: CreateMenuRequest // مدل برای ساخت منو جدید
    ): Response<Category> // بازگشت Category ایجاد شده (شامل title)

    @DELETE("restaurants/{id}/menu/{title}")
    suspend fun deleteRestaurantMenu(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Path("title") menuTitle: String // عنوان منو
    ): Response<MessageResponse>

    @PUT("restaurants/{id}/menu/{title}")
    suspend fun addItemToMenu(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Path("title") menuTitle: String,
        @Body addItemToMenuRequest: AddItemToMenuRequest
    ): Response<MessageResponse>

    @DELETE("restaurants/{id}/menu/{title}/{item_id}")
    suspend fun deleteItemFromMenu(@Header("Authorization") token: String, @Path("id") restaurantId: Int, @Path("title") title: String, @Path("item_id") itemId: Int): Response<MessageResponse>

    @GET("restaurants/{id}/orders")
    suspend fun getRestaurantOrders(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Query("status") status: String? = null,
        @Query("search") search: String? = null,
        @Query("user") customerName: String? = null,
        @Query("courier") courierName: String? = null
    ): Response<List<Order>>

    @PATCH("restaurants/orders/{order_id}")
    suspend fun updateOrderStatusByRestaurant(
        @Path("order_id") orderId: Int,
        @Body request: OrderStatusUpdateRequest,
        @Header("Authorization") token: String
    ): Response<MessageResponse>


    // Buyer APIs
    @POST("vendors")
    suspend fun getVendors(@Header("Authorization") token: String, @Body request: VendorListRequest?): Response<List<Restaurant>>

    @GET("vendors/{id}")
    suspend fun getVendorMenuDetails(@Header("Authorization") token: String, @Path("id") vendorId: String): Response<RestaurantMenuDetailsResponse>

    @POST("items")
    suspend fun listItems(@Header("Authorization") token: String, @Body request: Map<String, Any>?): Response<List<FoodItem>> // Assuming search, price, keywords

    @GET("items/{id}")
    suspend fun getItemDetails(@Header("Authorization") token: String, @Path("id") itemId: Int): Response<FoodItem>

    // Missing from YAML: Submit an order
    // @POST("orders")
    // suspend fun submitOrder(@Header("Authorization") token: String, @Body request: OrderSubmitRequest): Response<Order>

    // Missing from YAML: Get order history for buyer
    // @GET("orders/history")
    // suspend fun getOrderHistory(@Header("Authorization") token: String, @Query("search") search: String? = null, @Query("vendor") vendor: String? = null): Response<List<Order>>


    // Admin APIs
    @GET("admin/users")
    suspend fun getAllUsers(@Header("Authorization") token: String): Response<List<User>>

    @PATCH("admin/users/{id}/status")
    suspend fun updateUserStatus(
        @Path("id") userId: String,
        @Body request: UserStatusUpdateRequest,
        @Header("Authorization") token: String
    ): Response<String>

    @GET("admin/orders")
    suspend fun getAllOrders(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null,
        @Query("search") search: String? = null,
        @Query("vendor") vendor: String? = null,
        @Query("courier") courier: String? = null,
        @Query("customer") customer: String? = null
    ): Response<List<Order>>

    @GET("admin/transactions")
    suspend fun getAllTransactions(
        @Header("Authorization") token: String,
        @Query("search") search: String? = null,
        @Query("user") user: String? = null,
        @Query("method") method: String? = null,
        @Query("status") status: String? = null
    ): Response<List<Transaction>>

    @GET("admin/coupons")
    suspend fun getAllCoupons(@Header("Authorization") token: String): Response<List<Coupon>>

    @POST("admin/coupons")
    suspend fun createCoupon(
        @Body request: CreateCouponRequest,
        @Header("Authorization") token: String
    ): Response<Coupon>

    @GET("admin/coupons/{id}")
    suspend fun getCouponDetails(
        @Path("id") couponId: String,
        @Header("Authorization") token: String
    ): Response<Coupon>

    @PUT("admin/coupons/{id}")
    suspend fun updateCoupon(
        @Path("id") couponId: String,
        @Body request: UpdateCouponRequest,
        @Header("Authorization") token: String
    ): Response<Coupon>

    @DELETE("admin/coupons/{id}")
    suspend fun deleteCoupon(
        @Path("id") couponId: String,
        @Header("Authorization") token: String
    ): Response<String>
}