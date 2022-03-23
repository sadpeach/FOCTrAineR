package com.example.foctrainer.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.repository.ExerciseRepository
import com.example.foctrainer.repository.ScheduleRepository
import kotlinx.coroutines.launch

class ScheduleViewModel (private val repository: ScheduleRepository):ViewModel() {

    val allDates: LiveData<List<ScheduleModel>> = repository.allDates.asLiveData()

    fun getScheduleByDate(selectedDate:String): LiveData<List<ScheduleModel>> {
        return repository.getScheduleByDate(selectedDate).asLiveData()
    }

    fun createNewDate(date: ScheduleModel) = viewModelScope.launch {
        repository.createNewDate(date)
    }
}
class ScheduleViewModelFactory(private val repository: ScheduleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

