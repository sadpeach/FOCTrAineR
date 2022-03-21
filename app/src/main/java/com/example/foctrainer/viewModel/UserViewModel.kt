package com.example.foctrainer.viewModel

import androidx.lifecycle.*
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.entity.UserModel
import com.example.foctrainer.repository.ScheduleRepository
import com.example.foctrainer.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository):ViewModel() {

    val allUsers: LiveData<List<UserModel>> = repository.allUsers.asLiveData()

    fun createNewUser(user: UserModel) = viewModelScope.launch {
        repository.createNewUser(user)
    }

    fun checkIfUserExistByNameAndPassword(userName:String,password:String): UserModel {
        return repository.checkIfUserExistByNameAndPassword(userName,password)
    }

}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}
