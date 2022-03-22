package com.example.foctrainer.viewModel

import androidx.lifecycle.*
import com.example.foctrainer.entity.CompletedExerciseModel
import com.example.foctrainer.mapper.CompletedExerciseMapper
import com.example.foctrainer.repository.CompletedExerciseRepository
import kotlinx.coroutines.launch

class CompletedExerciseViewModel(private val repository: CompletedExerciseRepository):ViewModel() {
//    val chartSummary: LiveData<List<CompletedExerciseModel>> = repository.chartSummary.asLiveData()
    val completedExercise: LiveData<List<CompletedExerciseModel>> = repository.completedExercise.asLiveData()

    fun insertNewCompletedExercise(completedExercise: CompletedExerciseModel) = viewModelScope.launch {
        repository.insertNewCompletedExercise(completedExercise)
    }


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