package com.example.foctrainer.schedule

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
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.exercise.Exercise

class ScheduleCalendarAdapter :
    ListAdapter<ScheduleModel, ScheduleCalendarAdapter.ScheduleRecyclerViewHolder>(ScheduleComparator()) {

    companion object {
        val TAG = "ScheduleCalendarAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleRecyclerViewHolder {
        return ScheduleRecyclerViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ScheduleRecyclerViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        holder.itemView.setOnClickListener { v: View ->
            Log.d(TAG, "clicking ${current.exerciseId}")
            val intent = Intent(v.context, Exercise::class.java)
            intent.putExtra("exerciseId", current.exerciseId)
            intent.putExtra("scheduleId", current.id)
            v.context.startActivity(intent)
        }


        holder.itemView.setOnLongClickListener(OnLongClickListener {
            Log.d(TAG,"LongClick: ${current.exerciseId}")
            val intent = Intent(it.context, CreateScheduleActivity::class.java)
            intent.putExtra("scheduleId", current.id)
            it.context.startActivity(intent)
            true // returning true instead of false, works for me
        })

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

