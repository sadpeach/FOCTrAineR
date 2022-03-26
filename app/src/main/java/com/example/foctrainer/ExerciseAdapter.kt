package com.example.foctrainer


import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.R
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.exercise.Exercise
import com.example.foctrainer.schedule.CreateScheduleActivity


class ExerciseAdapter(private val exerciseList: ArrayList<ExerciseModel>):RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {


    companion object {
        val TAG = "ExerciseAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseAdapter.ExerciseViewHolder {
        return ExerciseViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val ex = exerciseList[position]
        holder.textExCategories.text = ex.name

        holder.itemView.setOnClickListener { v: View ->
            val exerciseId = exerciseList[position].id
        }
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    inner class ExerciseViewHolder (view : View): RecyclerView.ViewHolder(view){
        var textExCategories : TextView
        init {
            textExCategories = view.findViewById(R.id.tvExerciseCat)
        }
    }


}


