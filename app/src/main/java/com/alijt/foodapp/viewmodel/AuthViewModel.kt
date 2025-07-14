package com.alijt.foodapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alijt.foodapp.model.AuthResponse
import com.alijt.foodapp.model.LoginRequest
import com.alijt.foodapp.model.MessageResponse
import com.alijt.foodapp.model.ProfileUpdateRequest
import com.alijt.foodapp.model.RegisterRequest
import com.alijt.foodapp.model.User
import com.alijt.foodapp.repository.AuthRepository
import com.alijt.foodapp.utils.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // LiveData for Register
    private val _registerResult = MutableLiveData<Result<AuthResponse>>()
    val registerResult: LiveData<Result<AuthResponse>> = _registerResult

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _registerResult.value = repository.registerUser(request)
        }
    }
    private val _loginResult = MutableLiveData<Result<AuthResponse>>()
    val loginResult: LiveData<Result<AuthResponse>> = _loginResult
    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loginResult.value = repository.loginUser(request)
        }
    }

    private val _userProfile = MutableLiveData<Result<User>>()
    val userProfile: LiveData<Result<User>> = _userProfile


    fun fetchUserProfile() { // Corrected: removed 'request' parameter
        viewModelScope.launch {
            val token = sessionManager.getAuthToken() // Get token from SessionManager
            if (token != null) {
                _userProfile.value = repository.fetchUserProfile(token)
            } else {
                // Handle case where token is null (e.g., user not logged in)
                _userProfile.value = Result.failure(Exception("Authentication token not found."))
            }
        }
    }

    private val _profileUpdateResult = MutableLiveData<Result<MessageResponse>>()
    val profileUpdateResult: LiveData<Result<MessageResponse>> = _profileUpdateResult

    // Function to update user profile
    fun updateUserProfile(request: ProfileUpdateRequest) {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken() // Get token from SessionManager
            if (token != null) {
                _profileUpdateResult.value = repository.updateUserProfile(token, request)
            } else {
                // Handle case where token is null
                _profileUpdateResult.value = Result.failure(Exception("Authentication token not found."))
            }
        }
    }
}