// FoodApp/app/src/main/java/com/alijt/foodapp/view/LoginActivity.kt
package com.alijt.foodapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.alijt.foodapp.R
import com.alijt.foodapp.databinding.ActivityLoginBinding
import com.alijt.foodapp.model.LoginRequest
import com.alijt.foodapp.repository.AuthRepository
import com.alijt.foodapp.utils.SessionManager
import com.alijt.foodapp.viewmodel.AuthViewModel
import com.alijt.foodapp.viewmodel.AuthViewModelFactory
import com.alijt.foodapp.view.DashboardActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Initialize ViewModel and pass sessionManager to its factory
        val repository = AuthRepository()
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(repository, sessionManager)).get(AuthViewModel::class.java) // Corrected line: pass sessionManager

        // Handle navigation to RegisterActivity
        binding.textViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Handle Login button click
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        // Observe the login result from ViewModel
        authViewModel.loginResult.observe(this) { result ->
            result.onSuccess { authResponse ->
                Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()

                sessionManager.saveAuthToken(authResponse.token)
                sessionManager.saveUserId(authResponse.user.id)
                sessionManager.saveUserRole(authResponse.user.role)

                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }.onFailure { exception ->
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun performLogin() {
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_all_required_fields), Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(this, getString(R.string.enter_valid_phone), Toast.LENGTH_SHORT).show()
            return
        }

        val loginRequest = LoginRequest(
            phone = phone,
            password = password
        )

        authViewModel.login(loginRequest)
    }
}