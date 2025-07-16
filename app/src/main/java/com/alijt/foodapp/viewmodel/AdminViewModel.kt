package com.alijt.foodapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alijt.foodapp.model.*
import com.alijt.foodapp.repository.AdminRepository
import com.alijt.foodapp.utils.SessionManager
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: AdminRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // LiveData for various data lists
    private val _usersList = MutableLiveData<Result<List<User>>>()
    val usersList: LiveData<Result<List<User>>> = _usersList

    private val _ordersList = MutableLiveData<Result<List<Order>>>()
    val ordersList: LiveData<Result<List<Order>>> = _ordersList

    private val _transactionsList = MutableLiveData<Result<List<Transaction>>>()
    val transactionsList: LiveData<Result<List<Transaction>>> = _transactionsList

    private val _couponsList = MutableLiveData<Result<List<Coupon>>>()
    val couponsList: LiveData<Result<List<Coupon>>> = _couponsList

    // LiveData for specific operation results
    // تغییر نوع LiveData به Result<String>
    private val _userStatusUpdateResult = MutableLiveData<Result<String>>() // <-- تغییر
    val userStatusUpdateResult: LiveData<Result<String>> = _userStatusUpdateResult // <-- تغییر

    private val _couponCreateResult = MutableLiveData<Result<Coupon>>()
    val couponCreateResult: LiveData<Result<Coupon>> = _couponCreateResult

    private val _couponUpdateResult = MutableLiveData<Result<Coupon>>()
    val couponUpdateResult: LiveData<Result<Coupon>> = _couponUpdateResult

    // تغییر نوع LiveData به Result<String>
    private val _couponDeleteResult = MutableLiveData<Result<String>>() // <-- تغییر
    val couponDeleteResult: LiveData<Result<String>> = _couponDeleteResult // <-- تغییر

    private val _couponDetails = MutableLiveData<Result<Coupon>>()
    val couponDetails: LiveData<Result<Coupon>> = _couponDetails

    fun fetchAllUsers() {
        _usersList.value = Result.Loading
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                _usersList.value = repository.getAllUsers(token)
            } else {
                _usersList.value = Result.Failure(Exception("Authentication token not found."))
            }
        }
    }

    fun updateUserStatus(userId: String, status: String) {
        _userStatusUpdateResult.value = Result.Loading
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                val request = UserStatusUpdateRequest(status = status)
                _userStatusUpdateResult.value = repository.updateUserStatus(token, userId, request)
            } else {
                _userStatusUpdateResult.value = Result.Failure(Exception("Authentication token not found."))
            }
        }
    }

    fun fetchAllOrders(status: String? = null, search: String? = null, vendor: String? = null, courier: String? = null, customer: String? = null) {
        _ordersList.value = Result.Loading
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                _ordersList.value = repository.getAllOrders(token, status, search, vendor, courier, customer)
            } else {
                _ordersList.value = Result.Failure(Exception("Authentication token not found."))
            }
        }
    }

    fun fetchAllTransactions(search: String? = null, user: String? = null, method: String? = null, status: String? = null) {
        _transactionsList.value = Result.Loading
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                _transactionsList.value = repository.getAllTransactions(token, search, user, method, status)
            } else {
                _transactionsList.value = Result.Failure(Exception("Authentication token not found."))
            }
        }
    }

    fun fetchAllCoupons() {
        _couponsList.value = Result.Loading
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                _couponsList.value = repository.getAllCoupons(token)
            } else {
                _couponsList.value = Result.Failure(Exception("Authentication token not found."))
            }
        }
    }

    fun createCoupon(request: CreateCouponRequest) {
        _couponCreateResult.value = Result.Loading
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                _couponCreateResult.value = repository.createCoupon(token, request)
                if (_couponCreateResult.value is Result.Success) {
                    fetchAllCoupons()
                }
            } else {
                _couponCreateResult.value = Result.Failure(Exception("Authentication token not found."))
            }
        }
    }

    fun getCouponDetails(couponId: String) {
        _couponDetails.value = Result.Loading
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                _couponDetails.value = repository.getCouponDetails(token, couponId)
            } else {
                _couponDetails.value = Result.Failure(Exception("Authentication token not found."))
            }
        }
    }

    fun updateCoupon(couponId: String, request: UpdateCouponRequest) {
        _couponUpdateResult.value = Result.Loading
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                _couponUpdateResult.value = repository.updateCoupon(token, couponId, request)
                if (_couponUpdateResult.value is Result.Success) {
                    fetchAllCoupons()
                }
            } else {
                _couponUpdateResult.value = Result.Failure(Exception("Authentication token not found."))
            }
        }
    }

    fun deleteCoupon(couponId: String) {
        _couponDeleteResult.value = Result.Loading
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                _couponDeleteResult.value = repository.deleteCoupon(token, couponId)
                if (_couponDeleteResult.value is Result.Success) {
                    fetchAllCoupons()
                }
            } else {
                _couponDeleteResult.value = Result.Failure(Exception("Authentication token not found."))
            }
        }
    }
}