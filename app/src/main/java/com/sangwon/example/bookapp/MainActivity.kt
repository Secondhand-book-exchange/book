package com.sangwon.example.bookapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseApp
import com.sangwon.example.bookapp.Adapter.ThemeAdapter
import com.sangwon.example.bookapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener, ThemeAdapter.OnItemClickListener {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var themeAdapter: ThemeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        FirebaseApp.initializeApp(this)
        setListener()
        setCategoryRecyclerView()
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

    //이거 클릭하면 이동하는 해당 게시물 페이지로 이동하는건가?
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item: BookItem = adapter.getItem(position) as BookItem
        val intent = Intent(this, BookInfoActivity::class.java)
        intent.putExtra("BookCover", item.Img.toString())
        intent.putExtra("BookTitle", item.BookTitle)
        intent.putExtra("Author", item.Author)
        intent.putExtra("Subscript", item.Subscript)
        intent.putExtra("uid",item.uid)
        intent.putExtra("name",item.name)
        startActivity(intent)
    }

    private fun setListener(){
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
    private fun callBookList() {
        GlobalScope.launch(Dispatchers.Main) {
            val postItems = arrayListOf<BookItem>() // 데이터를 임시로 저장할 리스트

            val result = withContext(Dispatchers.IO) {
                db.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }

            for (document in result) {
                val bookTitle = document.getString("bookTitle")
                val author = document.getString("author")
                val bookStatus = document.getString("bookStatus")
                // val id = document.id 이거 왜 필요했지??
                //내가 쓰려고 가져 왔다가 안 썼나바
                val time = document.getTimestamp("timestamp")
                // 나중에 time에서 Date 뽑아내기
                val locate = document.getString("locate")
                val subscript = document.getString("subscript")
                imagePath = document.getString("image").toString()
                val isSale = document.getLong("isSale")!!.toInt()
                val category = document.getString("category")
                val name = document.getString("name")
                val uid = document.getString("uid")
                // 이미지를 등록하지 않은 경우 default 이미지
                if (imagePath == "") {
                    imagePath = "images/default.png"
                }
                val firebaseStorage = FirebaseStorage.getInstance()

                val storageReference = firebaseStorage.reference.child(imagePath)


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
                            Category = category ?: "",
                            type = isSale,
                            name = name ?: "",
                            uid = uid ?: ""
                        )
                        postItems.add(postItem)

                        // 모든 데이터를 가져왔을 때 어댑터에 추가하고 화면 업데이트
                        if (postItems.size == result.size()) {
                            for (item in postItems) {
                                adapter.addBook(item)
                            }
                            adapter.notifyDataSetChanged()

                            val desiredWidth =
                                View.MeasureSpec.makeMeasureSpec(listview.width, View.MeasureSpec.AT_MOST)

                            val listItem: View = adapter.getView(0, null, listview)
                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                            val totalHeight = listItem.measuredHeight * adapter.count

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
        themeAdapter.notifyDataSetChanged()
        themeAdapter.setOnItemClickListener(this)
    }

    override fun onItemClick(view: View, pos: Int) {
        val intent = Intent(this, BookListActivity::class.java)
        intent.putExtra("theme", themeAdapter.list[pos].theme)
        startActivity(intent)
    }
}