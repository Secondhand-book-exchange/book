package com.sangwon.example.bookapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sangwon.example.bookapp.databinding.ActivityBookListBinding

class BookListActivity : AppCompatActivity() {
    lateinit var binding: ActivityBookListBinding
    lateinit var category: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val type = intent.getStringExtra("type")?:""

        val list = BookListFragment(type)

        title = when(type){
            ""->"최근에 올라온 서적"
            "sales"->"거래 완료된 서적"
            "purchase"->"거래 중인 서적"
            "category"->{
                category = intent.getStringExtra("category").toString()
                list.setCategory(category)
                "$category 목록"
            }
            else->""
        }


        supportFragmentManager.beginTransaction().replace(R.id.list, list).commit()
    }
}