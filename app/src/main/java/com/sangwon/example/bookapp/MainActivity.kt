package com.sangwon.example.bookapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sangwon.example.bookapp.Adapter.BookListAdapter
import com.sangwon.example.bookapp.Adapter.ThemeAdapter
import com.sangwon.example.bookapp.Item.BookItem
import com.sangwon.example.bookapp.Item.BookTheme
import com.sangwon.example.bookapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
    ThemeAdapter.OnItemClickListener {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var adapter: BookListAdapter
    private lateinit var listview: ListView //언제 쓰지?
    private lateinit var themeAdapter:ThemeAdapter
    var db = Firebase.firestore
    private lateinit var imagePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        listview = binding.bookList
        adapter = BookListAdapter()

        listview.adapter = adapter

        callBookList()
        setListener()
        setThemeRecyclerView()
    }

    private fun setThemeRecyclerView() {
        val icons = arrayListOf<Int>(
            R.drawable.baseline_account_circle_24,
            R.drawable.baseline_home_24,
            R.drawable.baseline_map_24,
            R.drawable.baseline_search_24,
            R.drawable.baseline_add_24,
            R.drawable.baseline_notifications_none_24
        )
        val themes = arrayListOf<String>("동화", "잡지", "소설", "시", "인문학", "비문학")
        val spacingInPixel = resources.getDimensionPixelSize(R.dimen.single_theme_margin)
        binding.displayThemes.addItemDecoration(ThemeAdapter.HorizontalItemDecoration(spacingInPixel))
        binding.displayThemes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        themeAdapter = ThemeAdapter()
        binding.displayThemes.adapter = themeAdapter
        for (i in 0 until icons.size){
            themeAdapter.add(BookTheme(icons[i], themes[i]))
        }
        themeAdapter.notifyDataSetChanged()
        themeAdapter.setOnItemClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.profileImage.id -> startActivity(Intent(this, MyPageActivity::class.java))
            binding.themesBtn.id -> {}
            binding.bookBtn.id -> {}
            binding.menuBtn.id -> {}
        }
    }

    //이거 클릭하면 이동하는 해당 게시물 페이지로 이동하는건가?
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item: BookItem = adapter.getItem(position) as BookItem
        val intent = Intent(this, BookInfoActivity::class.java)
        intent.putExtra("BookCover", item.Img.toString())
        intent.putExtra("BookTitle", item.BookTitle)
        intent.putExtra("Author", item.Author)
        intent.putExtra("Subscript", item.Subscript)
        startActivity(intent)
    }

    private fun setListener(){
        // 마이페이지로 넘어가는 버튼 클릭 이벤트 처리
        binding.profileImage.setOnClickListener(this)
        binding.themesBtn.setOnClickListener(this)
        binding.bookBtn.setOnClickListener(this)
        binding.menuBtn.setOnClickListener(this)

        binding.bookList.onItemClickListener = this
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

    private fun callBookList() {
        GlobalScope.launch(Dispatchers.Main) {
            val postItems = arrayListOf<BookItem>() // 데이터를 임시로 저장할 리스트

            val result = withContext(Dispatchers.IO) {
                db.collection("Posts")
                    //.orderBy("timestamp", Query.Direction.DESCENDING) 아직 시간 속성 안 넣었어
                    .get()
                    .await()
            }

            for (document in result) {
                val bookTitle = document.getString("bookTitle")
                val author = document.getString("author")
                val bookStatus = document.getString("bookStatus")
                // val date = document.getString("date") 보류
                // val id = document.id 이거 왜 필요했지??
                val time = document.getString("time")
                val locate = document.getString("locate")
                val subscript = document.getString("subscript")
                val isSale = document.get("isSale") as Long
                imagePath = document.getString("image").toString()

                // 이미지를 등록하지 않은 경우 default 이미지
                if (imagePath == "") {
                    imagePath = "images/default.png"
                }
                val firebaseStorage = FirebaseStorage.getInstance()

                val storageReference = firebaseStorage.getReference().child(imagePath)

                storageReference.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val imageData = task.result
                        val postItem = BookItem(
                            Img = imageData,
                            BookTitle = bookTitle ?: "",
                            Author = author ?: "",
                            BookStatus = bookStatus ?: "",
                            Subscript = subscript ?: "",
                            Date = "",
                            Locate = "",
                            type = isSale.toInt(),
                        )
                        postItems.add(postItem)

                        // 모든 데이터를 가져왔을 때 어댑터에 추가하고 화면 업데이트
                        if (postItems.size == result.size()) {
                            for (item in postItems) {
                                adapter.addBook(item)
                            }
                            adapter.notifyDataSetChanged()

                            var totalHeight = 0
                            val desiredWidth =
                                View.MeasureSpec.makeMeasureSpec(listview.width, View.MeasureSpec.AT_MOST)

                            val listItem: View = adapter.getView(0, null, listview)
                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                            totalHeight = listItem.measuredHeight * adapter.count

                            val params: ViewGroup.LayoutParams = listview.layoutParams
                            params.height = totalHeight + listview.dividerHeight * (adapter.count - 1)
                            listview.layoutParams = params
                            listview.requestLayout()
                        }
                    } else {
                        Log.e("downloadUrl", "failed..")
                    }
                }
            }

        }
    }

    override fun onItemClick(view: View, pos: Int) {
        val intent = Intent(this, BookListActivity::class.java)
        intent.putExtra("theme", themeAdapter.list[pos].theme)
        startActivity(intent)
    }
}