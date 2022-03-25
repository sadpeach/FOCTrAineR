package com.example.foctrainer

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.exercise.Exercise

class MainRecyclerAdapter : ListAdapter<ExerciseModel, MainRecyclerAdapter.MainRecyclerViewHolder>(MainComparator()) {
        companion object {
        val TAG = "MainRecyclerAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainRecyclerViewHolder {
        return MainRecyclerViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MainRecyclerViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        holder.itemView.setOnClickListener { v: View ->
            Log.d(TAG, "clicking recyclerView item")
            v.context.startActivity(Intent(v.context, Exercise::class.java))
        }

    }

    class MainRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textExCategories: TextView = itemView.findViewById(R.id.tvExerciseCat)

        fun bind(ex: ExerciseModel) {
            textExCategories.text = ex.name
        }

        companion object {
            fun create(parent: ViewGroup): MainRecyclerViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_view, parent, false)
                return MainRecyclerViewHolder(view)
            }
        }
    }

    class MainComparator : DiffUtil.ItemCallback<ExerciseModel>() {
        override fun areItemsTheSame(oldItem: ExerciseModel, newItem: ExerciseModel): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ExerciseModel, newItem: ExerciseModel): Boolean {
            return oldItem.id == newItem.id
        }
    }
}
