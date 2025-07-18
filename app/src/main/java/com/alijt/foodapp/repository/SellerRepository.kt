package com.alijt.foodapp.repository

import com.alijt.foodapp.model.ErrorResponse
import com.alijt.foodapp.model.Restaurant
import com.alijt.foodapp.model.MessageResponse
import com.alijt.foodapp.model.FoodItem
import com.alijt.foodapp.model.Category
import com.alijt.foodapp.model.CreateMenuRequest
import com.alijt.foodapp.model.AddItemToMenuRequest
import com.alijt.foodapp.model.Order
import com.alijt.foodapp.model.OrderStatusUpdateRequest
import com.alijt.foodapp.model.RestaurantMenuDetailsResponse // اضافه شد
import com.alijt.foodapp.network.ApiService
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.utils.SessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken // اضافه شد
import retrofit2.Response

class SellerRepository(private val apiService: ApiService, private val sessionManager: SessionManager) {

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

    suspend fun getMyRestaurants(): Result<List<Restaurant>> {
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            safeApiCall(
                call = { apiService.getMyRestaurants("Bearer $token") },
                defaultErrorMessage = "Failed to fetch seller's restaurants"
            )
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }

    suspend fun createRestaurant(restaurant: Restaurant): Result<Restaurant> {
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            safeApiCall(
                call = { apiService.createRestaurant("Bearer $token", restaurant) },
                defaultErrorMessage = "Failed to create restaurant"
            )
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }

    suspend fun updateRestaurant(restaurant: Restaurant): Result<Restaurant> {
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            restaurant.id?.let { restaurantId ->
                safeApiCall(
                    call = { apiService.updateRestaurantDetails("Bearer $token", restaurantId, restaurant) },
                    defaultErrorMessage = "Failed to update restaurant"
                )
            } ?: Result.Failure(Exception("Restaurant ID is missing for update."))
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }

    // متد جدید برای دریافت آیتم‌های غذایی یک رستوران
    // از API: GET /vendors/{id} استفاده می‌کند و آیتم‌ها را از آن استخراج می‌کند.
    suspend fun getFoodItemsForRestaurant(restaurantId: Int): Result<List<FoodItem>> { // <-- اینجا اضافه شد
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            safeApiCall<RestaurantMenuDetailsResponse>( // Call apiService.getVendorMenuDetails
                call = { apiService.getVendorMenuDetails("Bearer $token", restaurantId.toString()) },
                defaultErrorMessage = "Failed to fetch food items for restaurant"
            ).let { result ->
                when (result) {
                    is Result.Success -> {
                        val responseData = result.data
                        val allFoodItems = mutableListOf<FoodItem>()
                        // اطلاعات آیتم‌های غذایی در additionalProperties در قالب Map<String, JsonElement> هستند
                        // باید هر JsonElement را به List<FoodItem> تبدیل کنیم
                        responseData.menuTitles.forEach { title ->
                            responseData.additionalProperties?.get(title)?.let { jsonElement ->
                                if (jsonElement.isJsonArray) {
                                    try {
                                        val itemsForCategory = Gson().fromJson<List<FoodItem>>(jsonElement, object : TypeToken<List<FoodItem>>() {}.type)
                                        allFoodItems.addAll(itemsForCategory)
                                    } catch (e: Exception) {
                                        return Result.Failure(Exception("Error parsing menu items for category $title: ${e.message}"))
                                    }
                                }
                            }
                        }
                        Result.Success(allFoodItems)
                    }
                    is Result.Failure -> {
                        Result.Failure(result.exception)
                    }
                    is Result.Loading -> { Result.Loading(null) } // این حالت نباید در اینجا اتفاق بیفتد
                }
            }
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }

    suspend fun addFoodItem(restaurantId: Int, foodItem: FoodItem): Result<FoodItem> {
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            safeApiCall(
                call = { apiService.addFoodItem("Bearer $token", restaurantId, foodItem) },
                defaultErrorMessage = "Failed to add food item"
            )
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }

    suspend fun editFoodItem(restaurantId: Int, foodItem: FoodItem): Result<FoodItem> {
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            foodItem.id?.let { itemId ->
                safeApiCall(
                    call = { apiService.editFoodItem("Bearer $token", restaurantId, itemId, foodItem) },
                    defaultErrorMessage = "Failed to edit food item"
                )
            } ?: Result.Failure(Exception("Food Item ID is missing for edit."))
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }

    suspend fun deleteFoodItem(restaurantId: Int, itemId: Int): Result<MessageResponse> {
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            safeApiCall(
                call = { apiService.deleteFoodItem("Bearer $token", restaurantId, itemId) },
                defaultErrorMessage = "Failed to delete food item"
            )
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }

    suspend fun createRestaurantMenu(restaurantId: Int, createMenuRequest: CreateMenuRequest): Result<Category> {
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            safeApiCall(
                call = { apiService.createRestaurantMenu("Bearer $token", restaurantId, createMenuRequest) },
                defaultErrorMessage = "Failed to create menu"
            )
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }

    suspend fun deleteRestaurantMenu(restaurantId: Int, menuTitle: String): Result<MessageResponse> {
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            safeApiCall(
                call = { apiService.deleteRestaurantMenu("Bearer $token", restaurantId, menuTitle) },
                defaultErrorMessage = "Failed to delete menu"
            )
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }

    suspend fun addItemToMenu(restaurantId: Int, menuTitle: String, addItemToMenuRequest: AddItemToMenuRequest): Result<MessageResponse> {
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            safeApiCall(
                call = { apiService.addItemToMenu("Bearer $token", restaurantId, menuTitle, addItemToMenuRequest) },
                defaultErrorMessage = "Failed to add item to menu"
            )
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }

    suspend fun getRestaurantOrders(
        restaurantId: Int,
        status: String? = null,
        search: String? = null,
        customerName: String? = null,
        courierName: String? = null
    ): Result<List<Order>> {
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            safeApiCall(
                call = { apiService.getRestaurantOrders("Bearer $token", restaurantId, status, search, customerName, courierName) },
                defaultErrorMessage = "Failed to fetch restaurant orders"
            )
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }

    suspend fun updateOrderStatusByRestaurant(orderId: Int, newStatus: OrderStatusUpdateRequest): Result<MessageResponse> {
        val token = sessionManager.getAuthToken()
        return if (token != null) {
            safeApiCall(
                call = { apiService.updateOrderStatusByRestaurant(orderId, newStatus, "Bearer $token") },
                defaultErrorMessage = "Failed to update order status"
            )
        } else {
            Result.Failure(Exception("Authentication token not found."))
        }
    }
}