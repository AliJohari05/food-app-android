package com.alijt.foodapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alijt.foodapp.repository.MenuRepository
import com.alijt.foodapp.utils.SessionManager
import kotlinx.coroutines.launch
// استفاده از 'as AppResult' برای رفع تداخل با kotlin.Result
import com.alijt.foodapp.model.Result as AppResult // <-- خط تغییر یافته

// ایمپورت مدل‌های دیگر:
import com.alijt.foodapp.model.RestaurantMenuDetailsResponse
import com.alijt.foodapp.model.ErrorResponse // اگر استفاده می‌شود
// ... سایر ایمپورت‌های مدل که MenuViewModel شما نیاز دارد

class MenuViewModel(
    private val repository: MenuRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _menuDetails = MutableLiveData<AppResult<RestaurantMenuDetailsResponse>>() // <-- از AppResult استفاده کنید
    val menuDetails: LiveData<AppResult<RestaurantMenuDetailsResponse>> = _menuDetails // <-- از AppResult استفاده کنید

    fun fetchMenuDetails(restaurantId: Int) {
        _menuDetails.value = AppResult.Loading // <-- از AppResult استفاده کنید
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                _menuDetails.value = repository.getRestaurantMenuDetails(token, restaurantId)
            } else {
                _menuDetails.value = AppResult.Failure(Exception("Authentication token not found.")) // <-- از AppResult استفاده کنید
            }
        }
    }
}