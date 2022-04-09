//package com.example.foctrainer.utils
//
//import android.app.Application
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.ViewModelProviders
//import com.example.foctrainer.databaseConfig.FocTrainerApplication
//import com.example.foctrainer.entity.ScheduleModel
//import com.example.foctrainer.viewModel.ScheduleViewModel
//import com.example.foctrainer.viewModel.ScheduleViewModelFactory
//import com.example.foctrainer.viewModel.UserViewModel
//import com.example.foctrainer.viewModel.UserViewModelFactory
//
//class NotifUtils: AppCompatActivity(){
//
//    private val scheduleViewModel: ScheduleViewModel by viewModels {
//        ScheduleViewModelFactory((application as FocTrainerApplication).scheduleRepository)
//    }
//
//
//    fun getReminderById() : ScheduleModel{
//        scheduleViewModel.
//    }
//}