package com.example.foctrainer.viewModel

import androidx.lifecycle.*
import com.example.foctrainer.entity.ScheduleExerciseModel
import com.example.foctrainer.mapper.ScheduleExerciseMapper
import com.example.foctrainer.repository.ScheduleExerciseRepository
import kotlinx.coroutines.flow.Flow

class ScheduleExerciseViewModel (private val repository: ScheduleExerciseRepository):ViewModel() {
    fun getScheduleExercise(selectedDate:String,userId:Int): LiveData<List<ScheduleExerciseModel>> {
        return repository.getScheduleExercise(selectedDate,userId).asLiveData()
    }
}

class ScheduleExerciseViewModelFactory(private val repository: ScheduleExerciseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ScheduleExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleExerciseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}