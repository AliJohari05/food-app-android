package com.alijt.foodapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alijt.foodapp.model.AuthResponse
import com.alijt.foodapp.model.LoginRequest
import com.alijt.foodapp.model.RegisterRequest
import com.alijt.foodapp.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

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
}