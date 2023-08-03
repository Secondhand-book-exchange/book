package com.sangwon.example.bookapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sangwon.example.bookapp.Adapter.ThemeAdapter
import com.sangwon.example.bookapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener, ThemeAdapter.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var themeAdapter: ThemeAdapter

    companion object {
        private const val REQUEST_USER_INFO = 1001
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        FirebaseApp.initializeApp(this)
        setListener()
        setCategoryRecyclerView()
        loadUserInfo()
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
            binding.themesBtn.id -> startActivity(Intent(this, BookListActivity::class.java))
            binding.bookBtn.id -> startActivity(Intent(this, BookListActivity::class.java))
            binding.menuBtn.id -> {}
        }
    }


    private fun setListener() {
        // 마이페이지로 넘어가는 버튼 클릭 이벤트 처리
        binding.profileImage.setOnClickListener(this)
        binding.themesBtn.setOnClickListener(this)
        binding.bookBtn.setOnClickListener(this)
        binding.menuBtn.setOnClickListener(this)

        binding.swipe.setOnRefreshListener(this)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            /**
             * 하단 네비게이션바
             */
            when (item.itemId) {
                R.id.home -> {
                }

                R.id.search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                }

                R.id.map -> {
                    startActivity(Intent(this, BookRegisterActivity::class.java))
                }

                R.id.notification -> {
                }
            }
            false
        }
    }


    private fun setCategoryRecyclerView() {
        val icons = arrayListOf<Int>(
            R.drawable.fairytale,
            R.drawable.magazine,
            R.drawable.novel,
            R.drawable.poet,
            R.drawable.humanities,
            R.drawable.baseline_notifications_none_24
        )
        val themes = resources.getStringArray(R.array.category)
        val spacingInPixel = resources.getDimensionPixelSize(R.dimen.single_theme_margin)
        binding.displayThemes.addItemDecoration(ThemeAdapter.HorizontalItemDecoration(spacingInPixel))
        binding.displayThemes.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        themeAdapter = ThemeAdapter()
        binding.displayThemes.adapter = themeAdapter
        for (i in 0 until icons.size) {
            themeAdapter.add(BookTheme(icons[i], themes[i]))
        }
    }


    override fun onItemClick(view: View, pos: Int) {
        val intent = Intent(this, BookListActivity::class.java)
        intent.putExtra("theme", themeAdapter.list[pos].theme)
        startActivity(intent)
    }


    private fun loadUserInfo() {
        val db = Firebase.firestore
        db.collection("users").document(Firebase.auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        // 사용자 정보를 UI에 설정
                        binding.nickname.text = it.name
                        Glide.with(this)
                            .load(it.profileImageUrl)
                            .into(binding.profileImage)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    override fun onRefresh() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.list, BookListFragment())
            .commitAllowingStateLoss()
        binding.swipe.isRefreshing = false
    }
}
