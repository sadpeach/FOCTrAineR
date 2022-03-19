package com.example.foctrainer.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import com.example.foctrainer.R
import com.example.foctrainer.databaseConfig.FocTrainerApplication
import com.example.foctrainer.viewModel.UserViewModel
import com.example.foctrainer.viewModel.UserViewModelFactory

class HomeFragment : Fragment() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        // homeFragment = FragmentHomeBinding.inflate(layoutInflater)
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        val tvName = view.findViewById<TextView>(R.id.name)
        val textEditHeight = view.findViewById<EditText>(R.id.editHeight)
        val textEditWeight = view.findViewById<EditText>(R.id.editWeight)
        val tvBmi = view.findViewById<TextView>(R.id.bmi)

        val name = this.arguments?.getString("name").toString()
        Log.d("HomeFragName", name)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}