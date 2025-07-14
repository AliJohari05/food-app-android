package com.alijt.foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alijt.foodapp.repository.MenuRepository
import com.alijt.foodapp.utils.SessionManager

class MenuViewModelFactory(
    private val repository: MenuRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}