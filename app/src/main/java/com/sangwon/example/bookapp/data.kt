package com.sangwon.example.bookapp

data class Posts(
    val BookTitle: String? = null,
    val Author:String? = null,
    val Locate: String? = null,

    val Subscript: String? = null,
    val Image: String? = null, //이거 어캐하냐?

    val Uid: String? = null,
    val Time: String? = null, //등록 시간

)
class Model {
    var imageUrl: String? = null

    internal constructor() {}
    constructor(imageUrl: String?) {
        this.imageUrl = imageUrl
    }
}