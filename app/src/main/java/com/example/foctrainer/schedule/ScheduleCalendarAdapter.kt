package com.example.foctrainer.schedule

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foctrainer.R
import com.example.foctrainer.entity.ScheduleExerciseModel
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.exercise.Exercise
import com.example.foctrainer.mapper.ScheduleExerciseMapper

class ScheduleCalendarAdapter :
    ListAdapter<ScheduleExerciseModel, ScheduleCalendarAdapter.ScheduleRecyclerViewHolder>(ScheduleComparator()) {

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
            val intent = Intent(v.context, Exercise::class.java)
            intent.putExtra("exerciseId", current.exercise_id)
            intent.putExtra("scheduleId", current.scheduleId)
            intent.putExtra("exerciseName",current.name)
            v.context.startActivity(intent)
        }


        holder.itemView.setOnLongClickListener(OnLongClickListener {
            val intent = Intent(it.context, CreateScheduleActivity::class.java)
            intent.putExtra("scheduleId", current.scheduleId)
            it.context.startActivity(intent)
            true
        })

    }

    class ScheduleRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val exerciseTitleTextView: TextView = itemView.findViewById(R.id.title)
        private val exerciseNotesTextView: TextView = itemView.findViewById(R.id.notes)
//        private val exerciseDetails:TextView = itemView.findViewById(R.id.setGoal)


        fun bind(schedule: ScheduleExerciseModel) {
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

    class ScheduleComparator : DiffUtil.ItemCallback<ScheduleExerciseModel>() {
        override fun areItemsTheSame(oldItem: ScheduleExerciseModel, newItem: ScheduleExerciseModel): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ScheduleExerciseModel, newItem: ScheduleExerciseModel): Boolean {
            return oldItem.exercise_id == newItem.exercise_id
        }
    }
}

