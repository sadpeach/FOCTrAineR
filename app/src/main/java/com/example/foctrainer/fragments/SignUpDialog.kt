package com.example.foctrainer.fragments

import android.app.Application
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.foctrainer.R
import com.example.foctrainer.viewModel.UserViewModel
import com.example.foctrainer.viewModel.UserViewModelFactory
import com.example.foctrainer.entity.UserModel
import com.google.android.material.textfield.TextInputEditText
import java.lang.Exception
import androidx.lifecycle.ViewModelProvider
import com.example.foctrainer.databaseConfig.FocTrainerApplication


class SignUpDialog : DialogFragment() {
    private lateinit var userViewModel:UserViewModel
    private lateinit var application:Application

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        application = activity!!.application as FocTrainerApplication
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE);
        }

        userViewModel = ViewModelProvider(this, UserViewModelFactory((application as FocTrainerApplication).userRepository))
            .get(UserViewModel::class.java)

        return inflater.inflate(R.layout.fragment_sign_up_dialog, container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.signUpButton).setOnClickListener{

            val userName = view.findViewById<TextInputEditText>(R.id.name).text.toString()
            val userPassword = view.findViewById<TextInputEditText>(R.id.password).text.toString()
            val height = view.findViewById<TextInputEditText>(R.id.height).text.toString().toFloat()
            val weight = view.findViewById<TextInputEditText>(R.id.weight).text.toString().toFloat()
            val bmi = view.findViewById<TextInputEditText>(R.id.bmi).text.toString().toFloat()

            try{
                var user:UserModel = UserModel(userName = userName,password = userPassword,height = height,weight = weight,bmi = bmi)
                userViewModel.createNewUser(user = user)

                Toast.makeText(activity, "Account successfully created", Toast.LENGTH_SHORT).show()

            }catch (e:Exception){
                Log.e(TAG,"Error occurred when creating new user: $e")
            }
            finally {
                dismiss()
            }


        }
    }


    companion object {
        private val TAG = "SignUpDialog"
    }

}