package com.sangwon.example.bookapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sangwon.example.bookapp.Adapter.BookListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * A fragment representing a list of Items.
 */
class BookListFragment : Fragment(), AdapterView.OnItemClickListener {
    private val adapter = BookListAdapter()
    private var keyword = ""
    private var category = ""
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)

        // Set the adapter
        if (view is ListView) {
            view.adapter = adapter
            view.onItemClickListener = this
            callBookList(view)
        }
        return view
    }

    /*private fun callBookList(view: view) {
        GlobalScope.launch(Dispatchers.Main) {
            val postItems = arrayListOf<BookItem>() // 데이터를 임시로 저장할 리스트

            val result = withContext(Dispatchers.IO) {
                val db = Firebase.firestore

                db.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }

            for (document in result) {
                val bookTitle = document.getString("bookTitle")
                Log.e("title", "bookTitle: $bookTitle")
                val author = document.getString("author")
                val bookStatus = document.getString("bookStatus")
                // val id = document.id 이거 왜 필요했지??
                //내가 쓰려고 가져 왔다가 안 썼나바
                val time = document.getTimestamp("timestamp")
                // 나중에 time에서 Date 뽑아내기
                val locate = document.getString("locate")
                val subscript = document.getString("subscript")
                var imagePath = document.getString("image").toString()
                val isSale = document.getLong("isSale")!!.toInt()
                val category = document.getString("category")
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
                            Locate = locate ?: "",
                            Category = category ?: "",
                            type = isSale,
                        )
                        postItems.add(postItem)

                        // 모든 데이터를 가져왔을 때 어댑터에 추가하고 화면 업데이트
                        if (postItems.size == result.size()) {
                            for (item in postItems) {
                                if (item.BookTitle.contains(keyword))
                                    adapter.addBook(item)
                            }
                            adapter.notifyDataSetChanged()

                            val desiredWidth =
                                View.MeasureSpec.makeMeasureSpec(
                                    view.width,
                                    View.MeasureSpec.AT_MOST
                                )

                            Log.d("locate2","why not")

                            val listItem: View = adapter.getView(0, null, view)
                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                            val totalHeight = listItem.measuredHeight * adapter.count

                            val params: ViewGroup.LayoutParams = view.layoutParams
                            params.height = totalHeight + view.dividerHeight * (adapter.count + 1)
                            view.layoutParams = params
                            view.requestLayout()
                        }
                    } else {
                        Log.e("downloadUrl", "failed..")
                    }
                }
            }
        }
    }*/
    private fun callBookList(view: ListView) {
        GlobalScope.launch(Dispatchers.Main) {
            val postItems = arrayListOf<BookItem>() // 데이터를 임시로 저장할 리스트

            val result = withContext(Dispatchers.IO) {
                val db = Firebase.firestore
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
                var imagePath = document.getString("image").toString()
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
                            Locate = locate?:"",
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
                                View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.AT_MOST)

                            val listItem: View = adapter.getView(0, null, view)
                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                            val totalHeight = listItem.measuredHeight * adapter.count

                            val params: ViewGroup.LayoutParams = view.layoutParams
                            params.height = totalHeight + view.dividerHeight * (adapter.count - 1)
                            view.layoutParams = params
                            view.requestLayout()
                        }
                    } else {
                        Log.e("downloadUrl", "failed..")
                    }
                }
            }

        }
    }

    //이거 클릭하면 이동하는 해당 게시물 페이지로 이동하는건가?
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item: BookItem = adapter.getItem(position) as BookItem
        val intent = Intent(context, BookInfoActivity::class.java)
        intent.putExtra("BookCover", item.Img.toString())
        intent.putExtra("BookTitle", item.BookTitle)
        intent.putExtra("Author", item.Author)
        intent.putExtra("Subscript", item.Subscript)
        intent.putExtra("uid",item.uid)
        intent.putExtra("name",item.name)
        startActivity(intent)
    }

    fun setKeyword(keyword: String) {
        this.keyword = keyword
    }

    fun setCategory(category: String) {
        this.category = category
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
    }
}