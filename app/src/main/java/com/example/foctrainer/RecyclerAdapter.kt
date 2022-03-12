package com.example.foctrainer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.viewModel.ExerciseViewModel

class RecyclerAdapter(private var exerciseList: List<ExerciseViewModel>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
        Log.d("IDK", exerciseList.toString())
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
        return exerciseList.size
    }

    class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val exerciseCategories : TextView = itemView.findViewById(R.id.tvExerciseCat)
    }
}