package com.sangwon.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SpinnerAdapter
import com.google.firebase.FirebaseApp
import com.sangwon.example.bookapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemClickListener {
    private val binding = ActivityMainBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        binding.searchBtn.setOnClickListener(this)
        binding.areaBtn.setOnClickListener(this)
        binding.bookList.setOnItemClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.searchBtn.id->
                startActivity(Intent(this, SearchActivity::class.java))
            binding.areaBtn.id->
                startActivity(Intent(this, SelectAreaActivity::class.java))
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val intent = Intent(this, BookInfoActivity::class.java)
        intent.putExtra("name", "bookName")
        startActivity(intent)
    }
}