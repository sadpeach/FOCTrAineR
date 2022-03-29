package com.example.foctrainer.viewModel

import androidx.lifecycle.*
import com.example.foctrainer.entity.ScheduleModel
import com.example.foctrainer.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*

class ScheduleViewModel (private val repository: ScheduleRepository):ViewModel() {

    val allDates: LiveData<List<ScheduleModel>> = repository.allDates.asLiveData()

    fun getScheduleByDate(selectedDate:String,userId:Int): LiveData<List<ScheduleModel>> {
        return repository.getScheduleByDate(selectedDate,userId).asLiveData()
    }

    fun createNewDate(date: ScheduleModel) = viewModelScope.launch {
        repository.createNewDate(date)
    }

//    val getDates: LiveData<List<String>> = repository.getDates.asLiveData()
    fun getDates(userId: Int):LiveData<List<String>>{
        return repository.getDates(userId).asLiveData()
    }

    fun getScheduledCountById(scheduleId:Int):LiveData<Int> {
        return repository.getScheduledCountById(scheduleId).asLiveData()
    }

    fun getScheduledExerciseById(scheduleId:Int,userId: Int): LiveData<ScheduleModel>{
        return repository.getScheduledExerciseById(scheduleId,userId).asLiveData()
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

