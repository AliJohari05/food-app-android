package com.alijt.foodapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alijt.foodapp.model.RestaurantMenuDetailsResponse
import com.alijt.foodapp.repository.MenuRepository
import com.alijt.foodapp.utils.SessionManager
import kotlinx.coroutines.launch

class MenuViewModel(
    private val repository: MenuRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _menuDetails = MutableLiveData<Result<RestaurantMenuDetailsResponse>>()
    val menuDetails: LiveData<Result<RestaurantMenuDetailsResponse>> = _menuDetails

    fun fetchMenuDetails(restaurantId: Int) {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                _menuDetails.value = repository.getRestaurantMenuDetails(token, restaurantId)
            } else {
                _menuDetails.value = Result.failure(Exception("Authentication token not found."))
            }
        }
    }
}