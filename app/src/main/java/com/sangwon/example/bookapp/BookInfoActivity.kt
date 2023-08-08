package com.sangwon.example.bookapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sangwon.example.bookapp.databinding.ActivityBookInfoBinding


class BookInfoActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityBookInfoBinding

    // 파이어베이스 인증과 Firestore 참조 생성
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private lateinit var profileImageUrl: String

    companion object {
        private const val REQUEST_USER_INFO = 1001
        private const val TAG = "BookInfoActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        binding.bookTitle.text = intent.getStringExtra("BookTitle")
        binding.Author.text = intent.getStringExtra("Author")
        binding.Subscript.text = intent.getStringExtra("Subscript")

        val uid = auth.currentUser?.uid

        //자기가 올린 게시물이라면 판매 종료 버튼 등장
        if(uid == intent.getStringExtra("uid"))
        {
            val chatButton = findViewById<FloatingActionButton>(R.id.chat)
            chatButton.visibility = View.GONE // 또는 View.GONE
            binding.salefinish.visibility = View.VISIBLE
        }

        binding.salefinish.setOnClickListener {
            val timestamp = intent.getStringExtra("timestamp")
            lateinit var documentId : String

            //우선 타임스탬프가 겹칠일이 없다는 전제하에 작업을 진행
            val postRef = db.collection("Posts")
                .addSnapshotListener { value, error ->
                    for (document in value!!.documents) {
                        if(document.get("timestamp").toString() == timestamp) {
                            documentId = document.id
                            val updates = hashMapOf(
                                "isSale" to 0
                            )
                            //업데이트하기
                            db.collection("Posts").document(documentId).update(updates as Map<String, Any>)
                                .addOnSuccessListener {
                                    Log.e("did","1")
                                }
                                .addOnFailureListener {
                                    Log.e("did","0")
                                }
                        }
                    }
                }
        }


        val uri = intent.getStringExtra("BookCover")
        if (uri != "defaultImage.jpeg") {
            Glide.with(this)
                .load(Uri.parse(uri))
                .into(binding.imageViewBookCover)
        }

        val currentUser = auth.currentUser
        currentUser?.let {
            // Firebase에서 사용자 정보를 읽어옴
            loadUserInfo(intent.getStringExtra("uid")?:"")
        }

        binding.chat.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.chat.id -> {
                val name = intent.getStringExtra("name")
                val uid = intent.getStringExtra("uid")
                val intent = Intent(this, ChatActivity::class.java)

                intent.putExtra("name", name)
                intent.putExtra("uid", uid)
                startActivity(intent)
            }
        }
    }

    // 판매자 정보를 파이어베이스로부터 가져와서 UI에 설정하는 함수
    private fun loadUserInfo(uid:String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        // 사용자 정보를 UI에 설정
                        binding.usernameTextView.text = it.name
                        profileImageUrl = "profile_images/$uid"

                        val storageReference = FirebaseStorage.getInstance().reference.child(profileImageUrl)

                        storageReference.downloadUrl.addOnCompleteListener { task ->
                            profileImageUrl = task.result.toString()
                            Glide.with(this)
                                .load(task.result)
                                .into(binding.profileImage)
                        }.addOnFailureListener {
                            binding.profileImage.setBackgroundResource(R.drawable.profile)
                            profileImageUrl = ""
                        }
                    }
                } else {
                    Log.d(BookInfoActivity.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(BookInfoActivity.TAG, "get failed with ", exception)
            }
    }
}
