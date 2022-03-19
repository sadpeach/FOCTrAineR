package com.example.foctrainer

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.exercise.Exercise

class RecyclerAdapter(private val exerciseList: ArrayList<String>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("IDK", exerciseList[position].toString())

        val ex = exerciseList[position]
        holder.textExCategories.text = ex.toString()

        holder.itemView.setOnClickListener { v: View ->
            Log.d("Click", "U have click")
            v.context.startActivity(Intent(v.context, Exercise::class.java))
            Toast.makeText(v.context,"Click", Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    inner class ViewHolder (view : View): RecyclerView.ViewHolder(view){
        var textExCategories : TextView
        init {
            textExCategories = view.findViewById(R.id.tvExerciseCat)
        }
    }
}