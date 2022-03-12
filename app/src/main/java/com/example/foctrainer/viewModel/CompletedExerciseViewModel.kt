package com.example.foctrainer.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.foctrainer.entity.CompletedExerciseModel
import com.example.foctrainer.repository.CompletedExerciseRepository

class CompletedExerciseViewModel(private val repository: CompletedExerciseRepository):ViewModel() {
    val completedExercise: LiveData<List<CompletedExerciseModel>> = repository.completedExercise.asLiveData()
}

class CompletedExerciseViewModelFactory(private val repository: CompletedExerciseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(CompletedExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CompletedExerciseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}