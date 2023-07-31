package com.sangwon.example.bookapp

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

data class User(
    val name: String = "",
    val userId: String = "",
    val passWord: String = "",
    val phoneNumber: String = ""
)


data class Message(
    var message: String?,
    var sendId: String?
){
    constructor():this("","")
}

class Model {
    var imageUrl: String? = null

    internal constructor() {}
    constructor(imageUrl: String?) {
        this.imageUrl = imageUrl
    }
}