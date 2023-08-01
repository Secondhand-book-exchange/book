package com.sangwon.example.bookapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sangwon.example.bookapp.Adapter.BookListAdapter
import com.sangwon.example.bookapp.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding:ActivitySearchBinding
    private lateinit var list:BookListFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        list = BookListFragment()

        binding.searchBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.searchBtn.id -> {
                val searchKeyWord = binding.searchText.text.toString()
                list.setKeyword(searchKeyWord)
                supportFragmentManager.beginTransaction().replace(binding.list.id, list).commit()
            }
        }
    }
}