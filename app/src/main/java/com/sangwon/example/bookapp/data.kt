package com.sangwon.example.bookapp

import android.net.Uri
import java.util.Date

data class Posts(
    val BookTitle: String? = null,
    val Author:String? = null,
    val BookStatus: String? = null,
    val Subscript: String? = null,
    val Image: String? = null,
    val IsSale: Int? = null,
    val Uid: String? = null,
    val Locate: String? = null,
    val timestamp: com.google.firebase.Timestamp? = null, //이 자료형 맞나?
    val Category : String? =null

)
data class BookItem(val Img: Uri,
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

data class BookTheme(val icon:Int, val theme:String)

data class User(
    val name: String = "",
    val userId: String = "",
    val passWord: String = "",
    val phoneNumber: String = ""
)

class Model {
    var imageUrl: String? = null

    internal constructor() {}
    constructor(imageUrl: String?) {
        this.imageUrl = imageUrl
    }
}