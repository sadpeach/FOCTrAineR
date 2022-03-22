package com.example.foctrainer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.databinding.ActivityLoginBinding
import com.example.foctrainer.entity.UserModel
import com.example.foctrainer.fragments.SignUpDialog
import com.example.foctrainer.viewModel.UserViewModel
import com.example.foctrainer.viewModel.UserViewModelFactory
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as FocTrainerApplication).userRepository)
    }

    companion object{
        private val TAG = "LoginActivity"
        private val PREFS_NAME = "SharedPreferenceFile"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val settings = getSharedPreferences(PREFS_NAME, 0)
        val hasLoggedIn = settings.getBoolean("hasLoggedIn", false)

        if (hasLoggedIn) {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {

            binding.LoginButton.setOnClickListener {

                var userName = binding.userName.text.toString()
                var password = binding.userPassword.text.toString()

                //add in identity check
                lifecycleScope.launch(Dispatchers.IO) {

                    val user: UserModel =
                        getExerciseNameAsync(userName = userName, password = password)

                    if (user != null) {

                        //saving user login to sharedPref
                        val settings =
                            getSharedPreferences(PREFS_NAME, 0) // 0 - for private mode
                        val editor = settings.edit()
                        editor.putBoolean("hasLoggedIn", true)
                        editor.putInt("userId",user.id)
                        editor.commit()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)

                        startActivity(intent)
                    } else {

                        this@LoginActivity.runOnUiThread(java.lang.Runnable {
                            binding.userName.text?.clear()
                            binding.userPassword.text?.clear()

                            Toast.makeText(
                                this@LoginActivity,
                                "$userName does not exists",
                                Toast.LENGTH_SHORT
                            ).show()
                        })

                    }
                }
            }

            binding.SignUpButton.setOnClickListener {
                SignUpDialog().show(supportFragmentManager, "Sign Up Form")
            }
        }
    }

    suspend fun getExerciseNameAsync(userName:String,password:String): UserModel = coroutineScope{
        val user = async { userViewModel.checkIfUserExistByNameAndPassword(userName = userName,password = password)}
        user.await()
    }

}