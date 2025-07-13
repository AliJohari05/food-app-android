package com.alijt.foodapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.alijt.foodapp.R // Import R for string resources
import com.alijt.foodapp.databinding.ActivityLoginBinding
import com.alijt.foodapp.model.LoginRequest // Import LoginRequest
import com.alijt.foodapp.repository.AuthRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.AuthViewModel
import com.alijt.foodapp.viewmodel.AuthViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel and SessionManager
        val repository = AuthRepository()
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(repository)).get(AuthViewModel::class.java)
        sessionManager = SessionManager(this)

        // Handle navigation to RegisterActivity
        binding.textViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish() // Finish LoginActivity so user can't go back to it after going to Register
        }

        // Handle Login button click
        binding.btnLogin.setOnClickListener {
            performLogin() // Call a separate function for login logic
        }

        // Observe the login result from ViewModel
        authViewModel.loginResult.observe(this) { result ->
            result.onSuccess { authResponse ->
                // Login successful: Always show localized success message
                Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()

                // Save auth token and user ID/role
                sessionManager.saveAuthToken(authResponse.token)
                sessionManager.saveUserId(authResponse.user_id)
                // The role comes directly from AuthResponse in successful login
                // Ensure the backend sends the role in uppercase like BUYER, SELLER
                sessionManager.saveUserRole(authResponse.user.role) // Assuming user object in AuthResponse has a 'role' field. This comes from backend.

                // Navigate to DashboardActivity
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish() // Finish LoginActivity
            }.onFailure { exception ->
                // Login failed: Always show localized generic failure message
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun performLogin() {
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Input validation
        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_all_required_fields), Toast.LENGTH_SHORT).show()
            return
        }

        // Basic phone number validation
        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(this, getString(R.string.enter_valid_phone), Toast.LENGTH_SHORT).show()
            return
        }

        // Create LoginRequest object
        val loginRequest = LoginRequest(
            phone = phone,
            password = password
        )

        // Call ViewModel to perform login
        authViewModel.login(loginRequest)
    }
}