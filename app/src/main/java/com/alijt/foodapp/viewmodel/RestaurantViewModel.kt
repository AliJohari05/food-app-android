package com.alijt.foodapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alijt.foodapp.model.AuthResponse
import com.alijt.foodapp.model.Restaurant
import com.alijt.foodapp.model.VendorListRequest
import com.alijt.foodapp.repository.AuthRepository
import com.alijt.foodapp.repository.RestaurantRepository
import com.alijt.foodapp.utils.SessionManager
import kotlinx.coroutines.launch

class RestaurantViewModel (
    private val repository: RestaurantRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _restaurants = MutableLiveData <Result<List<Restaurant>>>()
    val restaurants: LiveData<Result<List<Restaurant>>> = _restaurants

    fun fetchRestaurants(request : VendorListRequest){
        viewModelScope.launch {
            val token = sessionManager.getAuthToken() // Get token from SessionManager
            if (token != null) {
                _restaurants.value = repository.getRestaurants(token,request)
            }
            else{
                _restaurants.value = Result.failure(Exception("Authentication token not found."))
            }
        }
    }
}