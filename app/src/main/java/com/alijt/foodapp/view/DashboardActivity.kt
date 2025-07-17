package com.alijt.foodapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController // برای findNavController روی Activity/View
import androidx.navigation.fragment.NavHostFragment // برای NavHostFragment
import androidx.navigation.NavGraph
import androidx.navigation.NavInflater
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.ActivityDashboardBinding
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.view.dashboard.AdminDashboardFragment // اطمینان از ایمپورت شدن اینها به عنوان مقاصد
import com.alijt.foodapp.view.dashboard.BuyerDashboardFragment
import com.alijt.foodapp.view.dashboard.CourierDashboardFragment
import com.alijt.foodapp.view.dashboard.SellerDashboardFragment


class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        // Dynamically set the start destination based on user role
        val navInflater: NavInflater = navController.navInflater
        val graph: NavGraph = navInflater.inflate(R.navigation.nav_graph)

        val userRole = sessionManager.getUserRole()
        userRole?.let { role ->
            val welcomeMessageResId: Int
            val startDestinationId: Int

            when (role.uppercase()) {
                "BUYER" -> {
                    welcomeMessageResId = R.string.welcome_message_buyer
                    startDestinationId = R.id.buyerDashboardFragment
                }
                "SELLER" -> {
                    welcomeMessageResId = R.string.welcome_message_seller
                    startDestinationId = R.id.sellerDashboardFragment
                }
                "COURIER" -> {
                    welcomeMessageResId = R.string.welcome_message_courier
                    startDestinationId = R.id.courierDashboardFragment
                }
                "ADMIN" -> {
                    welcomeMessageResId = R.string.welcome_message_admin
                    startDestinationId = R.id.adminDashboardFragment
                }
                else -> {
                    Toast.makeText(this, getString(R.string.unknown_role_message), Toast.LENGTH_LONG).show()
                    sessionManager.logout()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    return
                }
            }

            graph.setStartDestination(startDestinationId)
            navController.graph = graph

            Toast.makeText(this, getString(welcomeMessageResId), Toast.LENGTH_LONG).show()
            binding.tvDashboardHeader.text = getString(welcomeMessageResId)

        } ?: run {
            Toast.makeText(this, getString(R.string.unknown_role_message), Toast.LENGTH_LONG).show()
            sessionManager.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // دکمه خروج از سیستم (همانند قبل)
        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}