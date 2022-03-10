package com.example.foctrainer.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.repository.ExerciseRepository

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {
    val allExercise: LiveData<List<ExerciseModel>> = repository.allExercise.asLiveData()
}

class ExerciseViewModelFactory(private val repository: ExerciseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExerciseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
