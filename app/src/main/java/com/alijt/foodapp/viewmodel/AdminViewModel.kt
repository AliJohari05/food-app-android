package com.alijt.foodapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alijt.foodapp.model.*
import com.alijt.foodapp.repository.AdminRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: AdminRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _usersList = MutableLiveData<Result<List<User>>>()
    val usersList: LiveData<Result<List<User>>> = _usersList

    private val _ordersList = MutableLiveData<Result<List<Order>>>()
    val ordersList: LiveData<Result<List<Order>>> = _ordersList

    private val _transactionsList = MutableLiveData<Result<List<Transaction>>>()
    val transactionsList: LiveData<Result<List<Transaction>>> = _transactionsList

    private val _couponsList = MutableLiveData<Result<List<Coupon>>>()
    val couponsList: LiveData<Result<List<Coupon>>> = _couponsList

    // تغییر نوع LiveData به Result<String>
    private val _userStatusUpdateResult = SingleLiveEvent<Result<String>>() // <-- این خط باید اینگونه باشد
    val userStatusUpdateResult: LiveData<Result<String>> = _userStatusUpdateResult

    private val _couponCreateResult = SingleLiveEvent<Result<Coupon>>()
    val couponCreateResult: LiveData<Result<Coupon>> = _couponCreateResult

    private val _couponUpdateResult = SingleLiveEvent<Result<Coupon>>()
    val couponUpdateResult: LiveData<Result<Coupon>> = _couponUpdateResult

    private val _couponDeleteResult = SingleLiveEvent<Result<String>>()
    val couponDeleteResult: LiveData<Result<String>> = _couponDeleteResult

    private val _couponDetails = SingleLiveEvent<Result<Coupon>>()
    val couponDetails: LiveData<Result<Coupon>> = _couponDetails

    fun fetchAllUsers() {
        _usersList.value = Result.Loading(null)
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
        _userStatusUpdateResult.value = Result.Loading(null)
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                val request = UserStatusUpdateRequest(status = status)
                // این فراخوانی اکنون Result<String> برمی‌گرداند.
                _userStatusUpdateResult.value = repository.updateUserStatus(token, userId, request)
            } else {
                _userStatusUpdateResult.value = Result.Failure(Exception("Authentication token not found."))
            }
        }
    }

    fun fetchAllOrders(status: String? = null, search: String? = null, vendor: String? = null, courier: String? = null, customer: String? = null) {
        _ordersList.value = Result.Loading(null)
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
        _transactionsList.value = Result.Loading(null)
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
        _couponsList.value = Result.Loading(null)
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
        _couponCreateResult.value = Result.Loading(null)
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
        _couponDetails.value = Result.Loading(null)
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
        _couponUpdateResult.value = Result.Loading(null)
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
        _couponDeleteResult.value = Result.Loading(null)
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

    fun updateOrderStatus(orderId: Int, newStatus: String) {
        // این LiveData برای پیام‌های عمومی است، بنابراین باید با Result<MessageResponse> سازگار باشد.
        _userStatusUpdateResult.value = Result.Loading(null) // <-- در اینجا Result<String> را منتشر می‌کنیم.
        viewModelScope.launch {
            val request = OrderStatusUpdateRequest(status = newStatus)
            val token = sessionManager.getAuthToken()
            if (token != null) {
                // repository.updateOrderStatusByRestaurant همچنان Result<MessageResponse> را برمی‌گرداند.
                // پس لازم است پیام آن را به String تبدیل کرده و به _userStatusUpdateResult.value بدهیم.
                val result = repository.updateOrderStatusByRestaurant(orderId, request, token)
                // اگر Result<MessageResponse> را به _userStatusUpdateResult (از نوع Result<String>) اختصاص دهیم، Type Mismatch داریم.
                // پس باید محتوای موفقیت را به String تبدیل کنیم.
                _userStatusUpdateResult.value = if (result is Result.Success) {
                    Result.Success(result.data.message) // <-- MessageResponse.message را به String تبدیل کن
                } else if (result is Result.Failure) {
                    Result.Failure(result.exception) // خطا را مستقیماً منتقل کن
                } else {
                    Result.Loading(null) // وضعیت Loading را حفظ کن
                }
            } else {
                _userStatusUpdateResult.value = Result.Failure(Exception("Authentication token not found."))
            }
        }
    }
}