package com.example.foctrainer.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foctrainer.repository.CompletedExerciseCalendarRepository

class CompletedExerciseCalendarViewModel (private val repository: CompletedExerciseCalendarRepository) : ViewModel() {

        //insert your functions

}
class CompletedExerciseCalendarViewModelFactory(private val repository: CompletedExerciseCalendarRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(CompletedExerciseCalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CompletedExerciseCalendarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}