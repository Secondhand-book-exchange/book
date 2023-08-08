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
import com.google.firebase.auth.ktx.auth
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
import java.util.Date
import com.google.firebase.Timestamp

/**
 * A fragment representing a list of Items.
 */
class BookListFragment(private val key: String = "") : Fragment(), AdapterView.OnItemClickListener {
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

    private fun callBookList(view: ListView) {
        GlobalScope.launch(Dispatchers.Main) {
            val postItems = arrayListOf<BookItem>() // 데이터를 임시로 저장할 리스트
            val auth = Firebase.auth.currentUser

            val result = withContext(Dispatchers.IO) {
                val db = Firebase.firestore
                db.collection("Posts")
                    .get()
                    .await()
            }

            for (document in result) {
                var imagePath = document.getString("image").toString()
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
                            BookTitle = document.getString("bookTitle") ?: "",
                            Author = document.getString("author") ?: "",
                            BookStatus = document.getString("bookStatus") ?: "",
                            Subscript = document.getString("subscript") ?: "",
                            Date = document.getTimestamp("timestamp")?.toDate() ?: Date(),
                            Locate = document.getString("locate") ?: "",
                            Category = document.getString("category") ?: "",
                            type = document.getLong("isSale")!!.toInt(),
                            name = document.getString("name") ?: "",
                            uid = document.getString("uid") ?: ""
                        )
                        postItems.add(postItem)

                        // 모든 데이터를 가져왔을 때 어댑터에 추가하고 화면 업데이트
                        if (postItems.size == result.size()) {
                            when (key) {
                                "" ->
                                    for (item in postItems)
                                        adapter.addBook(item)

                                "sales" ->
                                    for (item in postItems)
                                        if (item.uid == auth?.uid && item.type())
                                            adapter.addBook(item)

                                "purchase" ->
                                    for (item in postItems)
                                        if (item.uid == auth?.uid && !item.type())
                                            adapter.addBook(item)

                                "search" ->
                                    for (item in postItems)
                                        if (item.BookTitle.contains(keyword))
                                            adapter.addBook(item)

                                "category" ->
                                    for (item in postItems)
                                        if (item.Category==category)
                                            adapter.addBook(item)
                            }
                            adapter.notifyDataSetChanged()

                            val desiredWidth =
                                View.MeasureSpec.makeMeasureSpec(
                                    view.width,
                                    View.MeasureSpec.AT_MOST
                                )

                            if (adapter.count > 0) {
                                val listItem: View = adapter.getView(0, null, view)
                                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                                val totalHeight = listItem.measuredHeight * postItems.size

                                val params: ViewGroup.LayoutParams = view.layoutParams
                                params.height =
                                    totalHeight + view.dividerHeight * postItems.size
                                view.layoutParams = params
                                view.requestLayout()
                            }
                            view.isNestedScrollingEnabled = false
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
        intent.putExtra("uid", item.uid)
        intent.putExtra("name", item.name)
        val timestamp = Timestamp(item.Date).toString()
        intent.putExtra("timestamp", timestamp) //타임스탬프를 이해서 게시물 찾기
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