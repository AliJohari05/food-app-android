package com.alijt.foodapp.model

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure<out T>(val exception: Exception) : Result<T>()
    data class Loading<out T>(val dummy: Any? = null) : Result<T>()
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

inline fun <T> Result<T>.onFailure(action: (Exception) -> Unit): Result<T> {
    if (this is Result.Failure) {
        action(exception)
    }
    return this
}

inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) {
        action()
    }
    return this
}