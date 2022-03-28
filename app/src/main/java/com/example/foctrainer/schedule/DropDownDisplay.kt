package com.example.foctrainer.schedule

class DropDownDisplay(dropDownText:String,id:Int) {

    private var dropDownText:String = dropDownText
    private var id:Int = id

    fun getDropDownText():String {
        return dropDownText;
    }

    fun getId():Int {
        return id;
    }

    override fun toString() :String {
        return dropDownText;
    }
}
