package com.sangwon.example.bookapp.Item

import android.net.Uri

data class BookItem(val img:Uri, val title:String, val date:String, val note:String, val type:Int){
    fun getType():Boolean{
        return type==0
    }
    fun type():Boolean{
        return type==0
    }
}
