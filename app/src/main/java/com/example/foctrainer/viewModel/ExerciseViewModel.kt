package com.example.foctrainer.viewModel

import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.repository.ExerciseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {
    val allExercise: LiveData<List<ExerciseModel>> = repository.allExercise.asLiveData()

    fun getExerciseNameById(exerciseId: Int): LiveData<String> {
        return repository.getExerciseNameById(exerciseId).asLiveData()
    }
    fun createNewExercise(exercise: ExerciseModel) = viewModelScope.launch{
        repository.createNewExercise(exercise)
    }




//     fun getExerciseNameById(exerciseId: Int): String {
//         return repository.getExerciseNameById(exerciseId)
//    }
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
