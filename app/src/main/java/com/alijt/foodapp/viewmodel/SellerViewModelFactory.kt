package com.alijt.foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alijt.foodapp.repository.SellerRepository
import com.alijt.foodapp.utils.SessionManager

class SellerViewModelFactory(
    private val repository: SellerRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SellerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SellerViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}