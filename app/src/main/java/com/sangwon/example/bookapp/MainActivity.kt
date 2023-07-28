package com.sangwon.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sangwon.example.bookapp.Adapter.BookListAdapter
import com.sangwon.example.bookapp.Item.BookItem
import com.sangwon.example.bookapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemClickListener {
    private val binding by lazy{ ActivityMainBinding.inflate(layoutInflater)}
    private lateinit var adapter:BookListAdapter

    lateinit var listview: ListView //언제 쓰지?

    var db = Firebase.firestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var imagePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        listview = binding.bookList
        adapter = BookListAdapter()

        listview.adapter = adapter
        firebaseStorage = FirebaseStorage.getInstance()

        BookList()

        // 마이페이지로 넘어가는 버튼 클릭 이벤트 처리
        binding.myPageButton.setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }

        binding.areaBtn.setOnClickListener(this)
        binding.bookList.onItemClickListener = this
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.areaBtn.id->
                startActivity(Intent(this, SelectAreaActivity::class.java))
        }
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

    //이거 클릭하면 이동하는 해당 게시물 페이지로 이동하는건가?
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item:BookItem = adapter.getItem(position) as BookItem
        val intent = Intent(this, BookInfoActivity::class.java)
        intent.putExtra("BookCover", item.Img)
        intent.putExtra("BookTitle", item.BookTitle)
        intent.putExtra("Author", item.Author)
        intent.putExtra("Subscript", item.Subscript)
        startActivity(intent)
    }
    private fun BookList() {
        GlobalScope.launch(Dispatchers.Main) {
            val postItems = arrayListOf<BookItem>() // 데이터를 임시로 저장할 리스트

            val result = withContext(Dispatchers.IO) {
                db.collection("Posts")
                    //.orderBy("timestamp", Query.Direction.DESCENDING) 아직 시간 속성 안 넣었어
                    .get()
                    .await()
            }

            for (document in result) {
                val booktitle = document.getString("bookTitle")
                val author = document.getString("author")
                val bookStatus = document.getString("bookStatus")
                // val date = document.getString("date") 보류
                // val id = document.id 이거 왜 필요했지??
                val time = document.getString("time")
                val locate = document.getString("locate")
                val subscript = document.getString("subscript")
                imagePath = document.getString("image").toString()

                // 이미지를 등록하지 않은 경우 default 이미지
                if (imagePath == "") {
                    imagePath = "images/default.png"
                }

                val storageReference = firebaseStorage.getReference().child(imagePath)

                storageReference.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val imageData = task.result
                        val postItem = BookItem(
                            Img = imageData,
                            BookTitle = booktitle ?: "",
                            Author = author ?: "",
                            BookStatus = bookStatus ?: "",
                            Subscript = subscript ?: "",
                            Date = "",
                            Locate = "",
                            type = 1,
                        )
                        postItems.add(postItem)

                        // 모든 데이터를 가져왔을 때 어댑터에 추가하고 화면 업데이트
                        if (postItems.size == result.size()) {
                            for (item in postItems) {
                                adapter.addBook(item)
                            }
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        Log.e("downloadUrl", "failed..")
                    }
                }
            }

        }
    }
}