package com.sangwon.example.bookapp.Item

import android.net.Uri

data class BookItem(val Img:Uri,
                    val BookTitle:String,
                    val Author:String,
                    val Date:String,
                    val BookStatus:String,
                    val Subscript:String,
                    val Locate:String,
                    val Category:String,
                    val type:Int)
{
    fun type():Boolean{
        return type == 1
    }
}
