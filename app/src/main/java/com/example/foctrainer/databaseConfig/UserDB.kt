package com.example.foctrainer.databaseConfig

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.foctrainer.entity.UserModel

val DATABASE_NAME = "UserDB"
val TABLE_NAME = "Users"
val COL_ID = "id"
val COL_NAME = "name"
//val COL_AGE = "age"

class UserDB(var context: Context) :SQLiteOpenHelper(context, DATABASE_NAME, null, 1){
    override fun onCreate(p0: SQLiteDatabase?) {
//        val createTable = "CREATE TABLE" + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                COL_NAME + "VARCHAR(256))";
        p0?.execSQL("CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_NAME NVARCHAR)")

        p0?.execSQL("INSERT INTO $TABLE_NAME($COL_NAME)VALUES('Tom Tan')")
//        p0?.execSQL(createTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

//    fun insertUser(user : UserModel){
//        val db = this.writableDatabase
//        var userValues = ContentValues()
//        userValues.put(COL_NAME, user.name)
//        var dataResult = db.insert(TABLE_NAME, null, userValues)
//        if (dataResult == -1.toLong())
//            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
//        else
//            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
//    }

    @SuppressLint("Range")
    fun readUserData() : MutableList<UserModel>{
        var userList :MutableList<UserModel> = ArrayList()

        val db = this.readableDatabase
        val queryDB = "SELECT * FROM " + TABLE_NAME
        val executeQuery = db.rawQuery(queryDB, null)
        if (executeQuery.moveToFirst()){
            do {
                var user = UserModel()
                user.id = executeQuery.getString(executeQuery.getColumnIndex(COL_ID)).toInt()
                user.name = executeQuery.getString(executeQuery.getColumnIndex(COL_NAME))
                userList.add(user)
            }while (executeQuery.moveToNext())
        }
        executeQuery.close()
        db.close()
        return userList
    }
}