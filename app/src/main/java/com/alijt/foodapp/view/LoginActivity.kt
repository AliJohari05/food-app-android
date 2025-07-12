package com.alijt.foodapp.view
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alijt.foodapp.databinding.ActivityLoginBinding
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}