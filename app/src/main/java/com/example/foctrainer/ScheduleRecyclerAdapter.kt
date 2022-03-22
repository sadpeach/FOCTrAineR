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
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.exercise.Exercise

class ScheduleRecyclerAdapter : ListAdapter<ScheduleModel, ScheduleRecyclerAdapter.ScheduleRecyclerViewHolder>(ScheduleComparator()) {

    companion object {
        val TAG = "ScheduleRecyclerAdapter"
    }

<<<<<<< Updated upstream
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleRecyclerViewHolder {
        return ScheduleRecyclerViewHolder.create(parent)
    }
=======
        val myList = scheduleList[position]
        holder.textScheduleTitles.text = myList.toString()
        holder.textScheduldeSets.text = myList.toString()
        holder.textScheduleNotes.text = myList.toString()
        holder.textScheduleDates.text = myList.toString()
>>>>>>> Stashed changes

    override fun onBindViewHolder(holder: ScheduleRecyclerViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        holder.itemView.setOnClickListener { v: View ->
            Log.d(TAG, "clicking recyclerView item")
            v.context.startActivity(Intent(v.context, Exercise::class.java))

            //TODO: pass schedule ID to exercise activity here

            //Toast.makeText(v.context,"clicked", Toast.LENGTH_LONG).show()
        }
    }

    class ScheduleRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val exerciseTitleTextView: TextView = itemView.findViewById(R.id.title)
        private val exerciseNotesTextView: TextView = itemView.findViewById(R.id.notes)


        fun bind(schedule: ScheduleModel) {
            exerciseTitleTextView.text = schedule.title
            exerciseNotesTextView.text = schedule.notes

        }

        companion object {
            fun create(parent: ViewGroup): ScheduleRecyclerViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_view_exercise, parent, false)
                return ScheduleRecyclerViewHolder(view)
            }
        }
    }

    class ScheduleComparator : DiffUtil.ItemCallback<ScheduleModel>() {
        override fun areItemsTheSame(oldItem: ScheduleModel, newItem: ScheduleModel): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ScheduleModel, newItem: ScheduleModel): Boolean {
            return oldItem.exerciseId == newItem.exerciseId
        }
    }
}
