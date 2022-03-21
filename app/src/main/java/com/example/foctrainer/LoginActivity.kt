package com.example.foctrainer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foctrainer.databinding.ActivityLoginBinding
import com.example.foctrainer.fragments.SignUpDialog

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        var userName = binding.userName
        var password = binding.userPassword

        binding.LoginButton.setOnClickListener{
            //add in identity check
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        binding.SignUpButton.setOnClickListener{
//            SignUpDialog().show(supportFragmentManager, "Sign Up Form")
        }
    }
}