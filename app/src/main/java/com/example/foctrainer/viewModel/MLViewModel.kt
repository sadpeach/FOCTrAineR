package com.example.foctrainer.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foctrainer.repository.MLConfigRepository

class MLViewModel(private val repository: MLConfigRepository) : ViewModel(){
}

class MLViewModelFactory(private val repository: MLConfigRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MLViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MLViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
