package com.alijt.foodapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.model.Restaurant
import com.alijt.foodapp.model.VendorListRequest
import com.alijt.foodapp.repository.RestaurantRepository
import com.alijt.foodapp.utils.SessionManager
import kotlinx.coroutines.launch

class RestaurantViewModel(private val repository: RestaurantRepository, private val sessionManager: SessionManager) : ViewModel() {

    private val _restaurants = MutableLiveData<Result<List<Restaurant>>>()
    val restaurants: LiveData<Result<List<Restaurant>>> = _restaurants

    fun fetchRestaurants(request: VendorListRequest) {
        _restaurants.value = Result.Loading(null) // <-- اصلاح شد
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                _restaurants.value = repository.fetchRestaurants(token, request)
            } else {
                _restaurants.value = Result.Failure(Exception("Authentication token not found."))
            }
        }
    }
}