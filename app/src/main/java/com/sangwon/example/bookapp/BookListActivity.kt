package com.sangwon.example.bookapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BookListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        supportFragmentManager.beginTransaction().replace(R.id.list, BookListFragment()).commit()
    }
}