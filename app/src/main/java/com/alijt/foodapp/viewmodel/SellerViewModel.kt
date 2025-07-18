package com.alijt.foodapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alijt.foodapp.model.Restaurant
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.model.MessageResponse
import com.alijt.foodapp.model.FoodItem
import com.alijt.foodapp.model.Category
import com.alijt.foodapp.model.CreateMenuRequest
import com.alijt.foodapp.model.AddItemToMenuRequest
import com.alijt.foodapp.model.Order
import com.alijt.foodapp.model.OrderStatusUpdateRequest
import com.alijt.foodapp.repository.SellerRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class SellerViewModel(private val repository: SellerRepository, private val sessionManager: SessionManager) : ViewModel() {

    private val _myRestaurants = MutableLiveData<Result<List<Restaurant>>>()
    val myRestaurants: LiveData<Result<List<Restaurant>>> = _myRestaurants

    private val _operationResult = SingleLiveEvent<Result<Any>>()
    val operationResult: LiveData<Result<Any>> = _operationResult

    // LiveData برای لیست سفارشات یک رستوران (برای SellerOrdersFragment)
    private val _restaurantOrders = MutableLiveData<Result<List<Order>>>()
    val restaurantOrders: LiveData<Result<List<Order>>> = _restaurantOrders

    // LiveData برای لیست آیتم‌های غذایی یک رستوران (برای SellerMenusFragment)
    private val _restaurantFoodItems = MutableLiveData<Result<List<FoodItem>>>() // <-- اینجا تعریف شد
    val restaurantFoodItems: LiveData<Result<List<FoodItem>>> = _restaurantFoodItems // <-- اینجا تعریف شد

    // LiveData برای لیست دسته‌بندی‌های منو (برای SellerMenusFragment)
    private val _restaurantCategories = MutableLiveData<Result<List<Category>>>()
    val restaurantCategories: LiveData<Result<List<Category>>> = _restaurantCategories


    fun fetchMyRestaurants() {
        _myRestaurants.value = Result.Loading(null)
        viewModelScope.launch {
            _myRestaurants.value = repository.getMyRestaurants()
        }
    }

    fun createRestaurant(restaurant: Restaurant) {
        _operationResult.value = Result.Loading(null)
        viewModelScope.launch {
            val result = repository.createRestaurant(restaurant)
            _operationResult.value = result
            if (result is Result.Success) {
                fetchMyRestaurants()
            }
        }
    }

    fun updateRestaurant(restaurant: Restaurant) {
        _operationResult.value = Result.Loading(null)
        viewModelScope.launch {
            val result = repository.updateRestaurant(restaurant)
            _operationResult.value = result
            if (result is Result.Success) {
                fetchMyRestaurants()
            }
        }
    }

    // متد برای دریافت آیتم‌های غذایی یک رستوران
    // API: GET /restaurants/{id}/items (فرض می‌کنیم این API وجود دارد یا باید از API دیگر استخراج شود)
    fun fetchRestaurantFoodItems(restaurantId: Int) { // <-- اینجا اضافه شد
        _restaurantFoodItems.value = Result.Loading(null)
        viewModelScope.launch {
            // این بخش نیاز به یک API جدید در SellerRepository دارد.
            // فعلاً از یک متد placeholder در SellerRepository استفاده می‌کنیم.
            _restaurantFoodItems.value = repository.getFoodItemsForRestaurant(restaurantId) // <-- فراخوانی متد جدید
        }
    }

    fun addFoodItem(restaurantId: Int, foodItem: FoodItem) {
        _operationResult.value = Result.Loading(null)
        viewModelScope.launch {
            val result = repository.addFoodItem(restaurantId, foodItem)
            _operationResult.value = result
            if (result is Result.Success) {
                fetchRestaurantFoodItems(restaurantId) // رفرش لیست
            }
        }
    }

    fun editFoodItem(restaurantId: Int, foodItem: FoodItem) {
        _operationResult.value = Result.Loading(null)
        viewModelScope.launch {
            val result = repository.editFoodItem(restaurantId, foodItem)
            _operationResult.value = result
            if (result is Result.Success) {
                fetchRestaurantFoodItems(restaurantId) // رفرش لیست
            }
        }
    }

    fun deleteFoodItem(restaurantId: Int, itemId: Int) {
        _operationResult.value = Result.Loading(null)
        viewModelScope.launch {
            val result = repository.deleteFoodItem(restaurantId, itemId)
            _operationResult.value = result
            if (result is Result.Success) {
                fetchRestaurantFoodItems(restaurantId) // رفرش لیست
            }
        }
    }

    fun createRestaurantMenu(restaurantId: Int, createMenuRequest: CreateMenuRequest) {
        _operationResult.value = Result.Loading(null)
        viewModelScope.launch {
            val result = repository.createRestaurantMenu(restaurantId, createMenuRequest)
            _operationResult.value = result
            if (result is Result.Success) {
                // اگر API برای دریافت لیست دسته بندی‌ها وجود دارد، آن را فراخوانی کنید
                // fetchRestaurantCategories(restaurantId)
            }
        }
    }

    fun deleteRestaurantMenu(restaurantId: Int, menuTitle: String) {
        _operationResult.value = Result.Loading(null)
        viewModelScope.launch {
            val result = repository.deleteRestaurantMenu(restaurantId, menuTitle)
            _operationResult.value = result
            if (result is Result.Success) {
                // اگر API برای دریافت لیست دسته بندی‌ها وجود دارد، آن را فراخوانی کنید
                // fetchRestaurantCategories(restaurantId)
            }
        }
    }

    fun addItemToMenu(restaurantId: Int, menuTitle: String, addItemToMenuRequest: AddItemToMenuRequest) {
        _operationResult.value = Result.Loading(null)
        viewModelScope.launch {
            val result = repository.addItemToMenu(restaurantId, menuTitle, addItemToMenuRequest)
            _operationResult.value = result
            if (result is Result.Success) {
                fetchRestaurantFoodItems(restaurantId) // رفرش لیست
            }
        }
    }

    fun fetchRestaurantOrders(restaurantId: Int, status: String? = null, search: String? = null, customerName: String? = null, courierName: String? = null) {
        _restaurantOrders.value = Result.Loading(null)
        viewModelScope.launch {
            _restaurantOrders.value = repository.getRestaurantOrders(restaurantId, status, search, customerName, courierName)
        }
    }

    fun updateOrderStatus(orderId: Int, newStatus: String) {
        _operationResult.value = Result.Loading(null)
        viewModelScope.launch {
            val request = OrderStatusUpdateRequest(status = newStatus)
            val result = repository.updateOrderStatusByRestaurant(orderId, request)
            _operationResult.value = result
            if (result is Result.Success) {
                // پس از موفقیت، شاید بخواهید لیست سفارشات را رفرش کنید.
                // فرض می‌کنیم در SellerOrdersFragment، restaurantId در دسترس است.
                // fetchRestaurantOrders(currentRestaurantId)
            }
        }
    }
}