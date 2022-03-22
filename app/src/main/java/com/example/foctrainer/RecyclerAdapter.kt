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
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.exercise.Exercise

class RecyclerAdapter(private val exerciseList: ArrayList<ExerciseModel>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("Setup view1", exerciseList[0].name)
        Log.d("Setup view2", "yoyo")
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("Setup view3", "hihi")
        val ex = exerciseList[position]
        holder.textExCategories.text = ex.name

        holder.itemView.setOnClickListener { v: View ->
            val intent = Intent(v.context, Exercise::class.java)
            intent.putExtra("exerciseId", exerciseList[position].id)
            v.context.startActivity(Intent(v.context, Exercise::class.java))
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