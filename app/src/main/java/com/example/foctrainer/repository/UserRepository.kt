package com.example.foctrainer.repository

import androidx.annotation.WorkerThread
import com.example.foctrainer.entity.ExerciseModel
import com.example.foctrainer.entity.UserModel
import com.example.foctrainer.mapper.UserMapper
import kotlinx.coroutines.flow.Flow

class UserRepository (private val userMapper: UserMapper) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun createNewUser(user: UserModel) {
        userMapper.createNewUser(user)
    }

    val allUsers: Flow<List<UserModel>> = userMapper.getAllUsers()

    fun checkIfUserExistByNameAndPassword(userName:String,password:String): UserModel {
        return userMapper.checkIfUserExistByNameAndPassword(userName,password)
    }

}