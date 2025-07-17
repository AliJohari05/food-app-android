package com.alijt.foodapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alijt.foodapp.model.Restaurant
import com.alijt.foodapp.model.Result
import com.alijt.foodapp.model.MessageResponse
import com.alijt.foodapp.repository.SellerRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class SellerViewModel(private val repository: SellerRepository, private val sessionManager: SessionManager) : ViewModel() {

    private val _myRestaurants = MutableLiveData<Result<List<Restaurant>>>()
    val myRestaurants: LiveData<Result<List<Restaurant>>> = _myRestaurants

    private val _operationResult = SingleLiveEvent<Result<Any>>()
    val operationResult: LiveData<Result<Any>> = _operationResult

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

}