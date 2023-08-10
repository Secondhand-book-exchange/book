package com.sangwon.example.bookapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.sangwon.example.bookapp.Adapter.SearchAdapter
import com.sangwon.example.bookapp.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var list: BookListFragment

    private lateinit var searchRecord: SharedPreferences
    private lateinit var recordSet:String
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchRecord = getSharedPreferences("searchRecord", Context.MODE_PRIVATE)
        recordSet = searchRecord.getString("record", "")?:""

        searchAdapter = SearchAdapter()
        for (keyword in recordSet.split("{")) {
            searchAdapter.add(keyword)
        }
        binding.searchRecord.adapter = searchAdapter
        binding.searchRecord.setOnItemClickListener { _, _, pos, _ ->
            val keyword = searchAdapter.getItem(pos) as String
            binding.searchText.setText(keyword)
            binding.searchBtn.callOnClick()

            binding.list.isGone = false
            binding.searchRecord.isGone = true
        }
        binding.searchRecord.setOnItemLongClickListener { _, _, pos, _ ->
            searchAdapter.delete(pos)
            searchAdapter.notifyDataSetChanged()
            true
        }
        binding.searchText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.searchRecord.isGone = false
                binding.list.isGone = true
            } else {
                binding.list.isGone = false
                binding.searchRecord.isGone = true
            }
        }
        binding.searchText.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    binding.searchBtn.callOnClick()
                }
            }
            true
        }
        binding.searchBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.searchBtn.id -> {
                val searchKeyWord = binding.searchText.text.toString()
                list = BookListFragment("search")
                list.setKeyword(searchKeyWord)
                supportFragmentManager.beginTransaction().replace(binding.list.id, list).commit()
                binding.searchText.clearFocus()

                searchAdapter.add(searchKeyWord)
                searchRecord.edit().putString("record", searchAdapter.getSearchSet()).apply()
            }
        }
    }
}
