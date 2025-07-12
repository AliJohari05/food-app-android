package com.alijt.foodapp.view


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alijt.foodapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val rolesForApi = listOf("Buyer", "Seller", "Courier")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedRoleIndex = binding.spinnerRole.selectedItemPosition
        val roleForApi = rolesForApi[selectedRoleIndex]

        println("Selected role: $roleForApi")
        binding.textViewLogin.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text.toString()
            val phone = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()
            val address = binding.etAddress.text.toString()
            val email = binding.etEmail.text.toString()

        }

    }

}

