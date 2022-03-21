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

class ScheduleRecyclerAdapter(private val scheduleList: ArrayList<String>): RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_view_exercise, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("IDK", scheduleList[position].toString())

        val myList = scheduleList[position]
        holder.textScheduleTitles.text = myList.toString()
        holder.textScheduldeSets.text = myList.toString()
        holder.textScheduleNotes.text = myList.toString()
        holder.textScheduleDates.text = myList.toString()


        holder.itemView.setOnClickListener { v: View ->
            Log.d("Click", "U have click")
            v.context.startActivity(Intent(v.context, PlannerActivity::class.java))
            Toast.makeText(v.context,"Click", Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    inner class ViewHolder (view : View): RecyclerView.ViewHolder(view){
        var textScheduleTitles : TextView
        var textScheduleDates : TextView
        var textScheduleNotes : TextView
        var textScheduldeSets : TextView
        init {
            textScheduleTitles = view.findViewById(R.id.itemTitle)
            textScheduleDates = view.findViewById(R.id.dateText)
            textScheduleNotes = view.findViewById(R.id.tvNotes)
            textScheduldeSets = view.findViewById(R.id.no_of_sets)
        }
    }
}