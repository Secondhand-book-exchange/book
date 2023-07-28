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
class Model {
    var imageUrl: String? = null

    internal constructor() {}
    constructor(imageUrl: String?) {
        this.imageUrl = imageUrl
    }
}