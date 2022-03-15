package com.example.foctrainer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.mapper.ExerciseMapper

class RecyclerAdapter(private val exerciseList: List<ExerciseModel>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ex = exerciseList[position]
        holder.exerciseCategories.text = ex.name
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    class ViewHolder (view : View): RecyclerView.ViewHolder(view){
        val exerciseCategories : TextView = view.findViewById(R.id.tvExerciseCat)
    }
}