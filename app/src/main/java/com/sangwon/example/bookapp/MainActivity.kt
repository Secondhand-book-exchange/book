package com.sangwon.example.bookapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sangwon.example.bookapp.Adapter.BookListAdapter
import com.sangwon.example.bookapp.Adapter.ThemeAdapter
import com.sangwon.example.bookapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener, ThemeAdapter.OnItemClickListener {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var themeAdapter: ThemeAdapter
    var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        FirebaseApp.initializeApp(this)
        setListener()
        setThemeRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        supportFragmentManager.beginTransaction()
            .replace(R.id.list, BookListFragment())
            .commitAllowingStateLoss()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.profileImage.id -> startActivity(Intent(this, MyPageActivity::class.java))
            binding.themesBtn.id -> {}
            binding.bookBtn.id -> {}
            binding.menuBtn.id -> {}
        }
    }

    private fun setListener() {
        // 마이페이지로 넘어가는 버튼 클릭 이벤트 처리
        binding.profileImage.setOnClickListener(this)
        binding.themesBtn.setOnClickListener(this)
        binding.bookBtn.setOnClickListener(this)
        binding.menuBtn.setOnClickListener(this)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            /**
             * 하단 네비게이션바
             */
            when (item.itemId) {
                R.id.home -> {
                    false
                }

                R.id.search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    false
                }

                R.id.map -> {
                    startActivity(Intent(this, BookRegisterActivity::class.java))
                    false
                }

                R.id.notification -> {
                    false
                }

                else -> false
            }
        }
    }

    private fun setThemeRecyclerView() {
        val icons = arrayListOf<Int>(
            R.drawable.fairytale,
            R.drawable.magazine,
            R.drawable.novel,
            R.drawable.poet,
            R.drawable.humanities,
            R.drawable.baseline_notifications_none_24
        )
        val themes = arrayListOf<String>("동화", "잡지", "소설", "시", "인문학", "비문학")
        val spacingInPixel = resources.getDimensionPixelSize(R.dimen.single_theme_margin)
        binding.displayThemes.addItemDecoration(ThemeAdapter.HorizontalItemDecoration(spacingInPixel))
        binding.displayThemes.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        themeAdapter = ThemeAdapter()
        binding.displayThemes.adapter = themeAdapter
        for (i in 0 until icons.size) {
            themeAdapter.add(BookTheme(icons[i], themes[i]))
        }
        themeAdapter.notifyDataSetChanged()
        themeAdapter.setOnItemClickListener(this)
    }

    override fun onItemClick(view: View, pos: Int) {
        val intent = Intent(this, BookListActivity::class.java)
        intent.putExtra("theme", themeAdapter.list[pos].theme)
        startActivity(intent)
    }
}