package com.example.foctrainer


import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.exercise.Exercise

class WorkoutRecyclerAdapter : ListAdapter<ExerciseModel, WorkoutRecyclerAdapter.WorkoutRecyclerViewHolder>(WorkoutComparator()) {

    companion object {
        val TAG = "WorkoutRecyclerAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutRecyclerViewHolder {
        return WorkoutRecyclerViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: WorkoutRecyclerViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        holder.itemView.setOnClickListener { v: View ->
            Log.d(TAG, "clicking recyclerView item")
            val myIntent = Intent(v.context, InformationActivity::class.java)
            //v.context.startActivity(Intent(v.context, Exercise::class.java))
            v.context.startActivity(myIntent)

            //TODO: pass schedule ID to exercise activity here

            Toast.makeText(v.context,"clicked", Toast.LENGTH_LONG).show()
        }
    }

    class WorkoutRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val exerciseTitleTextView: TextView = itemView.findViewById(R.id.exerciseTitle)

        fun bind(workout: ExerciseModel) {
            exerciseTitleTextView.text = workout.name
        }

        companion object {
            fun create(parent: ViewGroup): WorkoutRecyclerViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_view_workout, parent, false)
                return WorkoutRecyclerViewHolder(view)
            }
        }
    }

    class WorkoutComparator : DiffUtil.ItemCallback<ExerciseModel>() {
        override fun areItemsTheSame(oldItem: ExerciseModel, newItem: ExerciseModel): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ExerciseModel, newItem: ExerciseModel): Boolean {
            return oldItem.mlId === newItem.mlId
        }
    }
}