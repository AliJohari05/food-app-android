// FoodApp/app/src/main/java/com/alijt/foodapp/viewmodel/AdminViewModel.kt
package com.alijt.foodapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alijt.foodapp.model.MessageResponse
import com.alijt.foodapp.model.User
import com.alijt.foodapp.model.UserStatusUpdateRequest
import com.alijt.foodapp.repository.AdminRepository
import com.alijt.foodapp.utils.SessionManager
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: AdminRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // LiveData for list of all users
    private val _usersList = MutableLiveData<Result<List<User>>>()
    val usersList: LiveData<Result<List<User>>> = _usersList

    // LiveData for user status update result
    private val _userStatusUpdateResult = MutableLiveData<Result<MessageResponse>>()
    val userStatusUpdateResult: LiveData<Result<MessageResponse>> = _userStatusUpdateResult

    fun fetchAllUsers() {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                _usersList.value = repository.getAllUsers(token)
            } else {
                _usersList.value = Result.failure(Exception("Authentication token not found."))
            }
        }
    }

    fun updateUserStatus(userId: String, status: String) {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                val request = UserStatusUpdateRequest(status = status)
                _userStatusUpdateResult.value = repository.updateUserStatus(token, userId, request)
            } else {
                _userStatusUpdateResult.value = Result.failure(Exception("Authentication token not found."))
            }
        }
    }
}