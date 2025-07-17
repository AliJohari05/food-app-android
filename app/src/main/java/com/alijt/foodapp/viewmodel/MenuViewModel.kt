package com.alijt.foodapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alijt.foodapp.repository.MenuRepository
import com.alijt.foodapp.utils.SessionManager
import kotlinx.coroutines.launch
import com.alijt.foodapp.model.Result as AppResult
import com.alijt.foodapp.model.RestaurantMenuDetailsResponse
import com.alijt.foodapp.model.ErrorResponse

class MenuViewModel(
    private val repository: MenuRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _menuDetails = MutableLiveData<AppResult<RestaurantMenuDetailsResponse>>()
    val menuDetails: LiveData<AppResult<RestaurantMenuDetailsResponse>> = _menuDetails

    fun fetchMenuDetails(restaurantId: Int) {
        _menuDetails.value = AppResult.Loading(null)
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                _menuDetails.value = repository.getRestaurantMenuDetails(token, restaurantId)
            } else {
                _menuDetails.value = AppResult.Failure(Exception("Authentication token not found."))
            }
        }
    }
}