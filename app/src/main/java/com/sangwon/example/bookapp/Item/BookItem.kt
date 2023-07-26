package com.sangwon.example.bookapp.Item

import android.net.Uri

data class BookItem(val img:Uri, val title:String, val author:String, val ISBN:String, val date:String, val subscript:String, val type:Int){
    fun type():Boolean{
        return type==0
    }
}
