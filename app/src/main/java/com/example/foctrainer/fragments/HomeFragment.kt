package com.example.foctrainer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foctrainer.R

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
//    fun displayUserDetails(){
//        val tvName = findViewById<TextView>(R.id.name)
//        val textEditHeight = findViewById<EditText>(R.id.editHeight)
//        val textEditWeight = findViewById<EditText>(R.id.editWeight)
//        val tvBmi = findViewById<TextView>(R.id.bmi)
//        tvName.text = ""
//        tvBmi.text = ""
//
//        userViewModel.allUsers.observe(this, Observer<List<UserModel>>() { users ->
//            userID = users[0].id
//            tvName.append(users[0].userName)
//            textEditHeight.setText(users[0].height.toString())
//            textEditWeight.setText(users[0].weight.toString())
//            tvBmi.append(users[0].bmi.toString())
//        })
}