// FoodApp/app/src/main/java/com/alijt/foodapp/view/RegisterActivity.kt
package com.alijt.foodapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.ActivityRegisterBinding
import com.alijt.foodapp.model.RegisterRequest
import com.alijt.foodapp.repository.AuthRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.AuthViewModel
import com.alijt.foodapp.viewmodel.AuthViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        // Initialize ViewModel and SessionManager
        val repository = AuthRepository()
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(repository,sessionManager)).get(AuthViewModel::class.java)
        sessionManager = SessionManager(this)

        // Set up spinner for user roles
        val userRolesArray = resources.getStringArray(R.array.user_roles)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userRolesArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter

        // Handle navigation to LoginActivity
        binding.textViewLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Finish RegisterActivity so user can't go back to it after going to Login
        }

        // Handle Register button click
        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        // Observe the registration result from ViewModel
        authViewModel.registerResult.observe(this) { result ->
            result.onSuccess { authResponse ->
                // Registration successful
                Toast.makeText(this, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
                // Save auth token and user ID/role
                sessionManager.saveAuthToken(authResponse.token)
                sessionManager.saveUserId(authResponse.user_id)
                // Determine the role based on spinner selection to save it
                val selectedRoleIndex = binding.spinnerRole.selectedItemPosition
                val selectedRole = resources.getStringArray(R.array.user_roles)[selectedRoleIndex]
                sessionManager.saveUserRole(selectedRole) // Save the selected role
                // Navigate to Dashboard or Login based on requirement, here navigating to Login
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }.onFailure { exception ->
                // Registration failed
                Toast.makeText(this, exception.message ?: getString(R.string.registration_failed), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerUser() {
        val fullName = binding.etFullName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        val selectedRoleIndex = binding.spinnerRole.selectedItemPosition
        // Get the English value of the role from strings.xml
        val rolesForApi = resources.getStringArray(R.array.user_roles)
        val roleForApi = rolesForApi[selectedRoleIndex].lowercase() // Convert to lowercase as per YAML enum (buyer, seller, courier)

        // Input validation
        if (fullName.isEmpty() || phone.isEmpty() || password.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_all_required_fields), Toast.LENGTH_SHORT).show()
            return
        }

        // Basic phone number validation (e.g., check length or format)
        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(this, getString(R.string.enter_valid_phone), Toast.LENGTH_SHORT).show()
            return
        }

        // Basic email validation if provided
        if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show()
            return
        }

        // Password strength (example: min 6 characters)
        if (password.length < 6) {
            Toast.makeText(this, getString(R.string.password_min_length), Toast.LENGTH_SHORT).show()
            return
        }

        // Create RegisterRequest object
        val registerRequest = RegisterRequest(
            full_name = fullName,
            phone = phone,
            password = password,
            role = roleForApi,
            address = address,
            email = if (email.isEmpty()) null else email
        )

        // Call ViewModel to perform registration
        authViewModel.register(registerRequest)
    }
}