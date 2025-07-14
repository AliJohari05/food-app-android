package com.alijt.foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alijt.foodapp.repository.AuthRepository
import com.alijt.foodapp.repository.RestaurantRepository
import com.alijt.foodapp.utils.SessionManager

class RestaurantViewModelFactory(
    private val repository: RestaurantRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RestaurantViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}