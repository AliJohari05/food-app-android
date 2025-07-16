package com.alijt.foodapp

import android.app.Application
import com.alijt.foodapp.network.RetrofitClient

class FoodApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
    }
}