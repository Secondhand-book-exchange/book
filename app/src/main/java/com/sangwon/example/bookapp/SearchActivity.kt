package com.sangwon.example.bookapp

import android.os.Bundle
import android.view.View
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import com.sangwon.example.bookapp.Adapter.MainBookListAdapter
import com.sangwon.example.bookapp.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding:ActivitySearchBinding
    lateinit var adapter: MainBookListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MainBookListAdapter()
        binding.searchBookList.adapter = adapter

        binding.searchBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.searchBtn.id -> {
                val searchKeyWord = binding.searchText.text.toString()
            }
        }
    }
}