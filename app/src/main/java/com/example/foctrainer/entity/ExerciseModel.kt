package com.example.foctrainer.entity

class ExerciseModel (id: Int, name: String,mlId:Int){

    private var id : Int = id
    private var name: String = name
    private var mlId : Int = id

    fun getId():Int{
        return this.id
    }

    fun getName():String{
        return this.name
    }

    fun setName(name:String){
        this.name = name
    }

    fun setId(id:Int){
        this.id = id
    }

    fun getMlId():Int{
        return this.mlId
    }

    fun setMlId(mlId: Int){
        this.mlId = mlId
    }
}