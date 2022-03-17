package com.example.foctrainer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.example.foctrainer.R
import com.example.foctrainer.RecyclerAdapter
import com.example.foctrainer.databinding.ActivityMainBinding
import com.example.foctrainer.databinding.FragmentHomeBinding
import com.example.foctrainer.entity.ExerciseModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class HomeFragment : Fragment() {


    lateinit var homeFragment: FragmentHomeBinding

    private lateinit var tvName: EditText
    private lateinit var binding: ActivityMainBinding
    private lateinit var lineValuesList: ArrayList<Entry>
    private lateinit var lineDataSet: LineDataSet
    private lateinit var dataLine: LineData
    private val exList = ArrayList<ExerciseModel>()
    private lateinit var exerciseAdapter: RecyclerAdapter
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        homeFragment = FragmentHomeBinding.inflate(layoutInflater)
//        return homeFragment.root
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val tvName : TextView = view.findViewById(R.id.name)
        val textEditHeight : EditText = view.findViewById(R.id.editHeight)
        val textEditWeight : EditText = view.findViewById(R.id.editWeight)
        val tvBmi : TextView = view.findViewById(R.id.bmi)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
//    fun displayUserDetails(view: View){
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