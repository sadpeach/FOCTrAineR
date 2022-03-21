package com.example.foctrainer.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.foctrainer.R
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.viewModel.UserViewModel
import com.example.foctrainer.viewModel.UserViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.example.foctrainer.entity.UserModel
import com.example.foctrainer.viewModel.ExerciseViewModel
import com.example.foctrainer.viewModel.ExerciseViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import java.lang.Exception


class SignUpDialog : DialogFragment() {

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((requireActivity() as FocTrainerApplication).userRepository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG,"Starting Sign Up Form")
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up_dialog, container)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE);
        }

        val userName = view.findViewById<TextInputEditText>(R.id.name).text.toString()
        val userPassword = view.findViewById<TextInputEditText>(R.id.password).text.toString()
        val height = view.findViewById<TextInputEditText>(R.id.height).text as Float
        val weight = view.findViewById<TextInputEditText>(R.id.weight).text as Float
        val bmi = view.findViewById<TextInputEditText>(R.id.bmi).text as Float
//        val age = view.findViewById<TextView>(R.id.age).text

        view.findViewById<Button>(R.id.signUpButton).setOnClickListener{

            try{
                var user = UserModel(userName = userName,password = userPassword,height = height,weight = weight,bmi = bmi)
                userViewModel.createNewUser(user = user)

                Toast.makeText(activity, "Account successfully created", Toast.LENGTH_SHORT).show()

            }catch (e:Exception){
                Log.e(TAG,"Error occurred when creating new user")
            }


        }
    }


    companion object {
        private val TAG = "SignUpDialog"
    }

}