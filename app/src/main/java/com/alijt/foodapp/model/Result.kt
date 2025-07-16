package com.alijt.foodapp.model // این خط باید دقیقاً همین باشد

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// توابع Extension برای مدیریت آسان‌تر وضعیت‌های Result
// این توابع باید در همین فایل Result.kt یا در فایلی جداگانه در همان پکیج قرار بگیرند.
// قرار دادن آن‌ها در همین فایل، ساده‌ترین راه برای اطمینان از دسترس‌پذیری است.

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