package com.example.foctrainer.mapper

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foctrainer.entity.UserModel
import kotlinx.coroutines.flow.Flow

@Dao
interface UserMapper {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createNewUser(user: UserModel)

    @Query("SELECT * FROM UserTable")
    fun getAllUsers(): Flow<List<UserModel>>

}