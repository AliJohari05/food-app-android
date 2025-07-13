package com.alijt.foodapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.ActivityDashboardBinding
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.view.dashboard.AdminDashboardFragment // We will create these
import com.alijt.foodapp.view.dashboard.BuyerDashboardFragment // We will create these
import com.alijt.foodapp.view.dashboard.CourierDashboardFragment // We will create these
import com.alijt.foodapp.view.dashboard.SellerDashboardFragment // We will create these


class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Get user role from session
        val userRole = sessionManager.getUserRole()

        // Display welcome message based on role and load appropriate fragment
        userRole?.let { role ->
            val fragment: Fragment
            val welcomeMessageResId: Int

            when (role.uppercase()) { // Convert to uppercase for comparison with BUYER, SELLER, etc.
                "BUYER" -> {
                    welcomeMessageResId = R.string.welcome_message_buyer
                    fragment = BuyerDashboardFragment() // Placeholder fragment
                }
                "SELLER" -> {
                    welcomeMessageResId = R.string.welcome_message_seller
                    fragment = SellerDashboardFragment() // Placeholder fragment
                }
                "COURIER" -> {
                    welcomeMessageResId = R.string.welcome_message_courier
                    fragment = CourierDashboardFragment() // Placeholder fragment
                }
                "ADMIN" -> {
                    welcomeMessageResId = R.string.welcome_message_admin
                    fragment = AdminDashboardFragment() // Placeholder fragment
                }
                else -> {
                    // Handle unknown role
                    Toast.makeText(this, getString(R.string.unknown_role_message), Toast.LENGTH_LONG).show()
                    sessionManager.logout()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    return
                }
            }
            Toast.makeText(this, getString(welcomeMessageResId), Toast.LENGTH_LONG).show()

            // Load the appropriate fragment into the container
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // R.id.fragment_container is from activity_dashboard.xml
                .commit()

            // Update header text based on role (optional)
            binding.tvDashboardHeader.text = getString(welcomeMessageResId)

        } ?: run {
            // No role found, redirect to login
            Toast.makeText(this, getString(R.string.unknown_role_message), Toast.LENGTH_LONG).show()
            sessionManager.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Handle Logout Button Click
        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
            startActivity(intent)
            finish()
        }
    }
}