package com.sangwon.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.google.firebase.FirebaseApp
import com.sangwon.example.bookapp.Adapter.MainBookListAdapter
import com.sangwon.example.bookapp.Item.BookItem
import com.sangwon.example.bookapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemClickListener {
    private val binding = ActivityMainBinding.inflate(layoutInflater)
    private lateinit var adapter:MainBookListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        adapter = MainBookListAdapter()
        binding.bookList.adapter = adapter

        binding.searchBtn.setOnClickListener(this)
        binding.registerBtn.setOnClickListener(this)
        binding.areaBtn.setOnClickListener(this)
        binding.bookList.onItemClickListener = this
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.searchBtn.id->
                startActivity(Intent(this, SearchActivity::class.java))
            binding.areaBtn.id->
                startActivity(Intent(this, SelectAreaActivity::class.java))
            binding.registerBtn.id ->
                startActivity(Intent(this, BookRegisterActivity::class.java))
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item:BookItem = adapter.getItem(position) as BookItem
        val intent = Intent(this, BookInfoActivity::class.java)
        intent.putExtra("BookCover", item.img)
        intent.putExtra("BookTitle", item.title)
        intent.putExtra("Author", item.author)
        intent.putExtra("ISBN", item.ISBN)
        intent.putExtra("Subscript", item.subscript)
        startActivity(intent)
    }
}