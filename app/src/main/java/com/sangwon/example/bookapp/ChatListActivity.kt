package com.sangwon.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sangwon.example.bookapp.Adapter.ChatListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChatListActivity : AppCompatActivity() {
    lateinit var listview: ListView
    lateinit var adapter: ChatListAdapter
    var db = Firebase.firestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var imagePath: String
    val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        listview = findViewById(R.id.listview)
        adapter = ChatListAdapter()
        listview.adapter = adapter

        firebaseStorage = FirebaseStorage.getInstance()

        GlobalScope.launch(Dispatchers.Main) {
            val chatItems = arrayListOf<Chat>()

            val userDocRef = db.collection("Chats").document("${auth.currentUser?.uid}")


            //유저 정보 문서 1개 가져옴 -> ChatRoom 배열을 가져와서 for문을 돌리면서 chatItem을 만든 후 집어넣어
            db.collection("users").document("${auth.currentUser?.uid}")
                .get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val chatRoomArray = documentSnapshot.get("ChatRoom") as ArrayList<String>?

                        // 널체크인데 에러나면 여기다!!
                        if (!chatRoomArray?.isEmpty()!!) {
                            for (chatroom in chatRoomArray) {
                                // chatroom에서 다른 유저의 uid 뽑아오기
                                val uid = chatroom.substring(0, 28)

                                //해당 유저의 정보에 접근해서 chat만들기
                                db.collection("users").document("${uid}")
                                    .get().addOnSuccessListener { documentSnapshot ->
                                        val name = documentSnapshot.getString("name")

                                        imagePath =
                                            documentSnapshot.getString("profileImageUrl").toString()
                                        // 이미지를 등록하지 않은 경우 default 이미지
                                        if (imagePath == "") {
                                            imagePath = "profile_images/default.jpg"
                                        }
                                        Log.e("imagePath", "${imagePath}")
                                        val storageReference =
                                            firebaseStorage.getReference().child(imagePath)
                                        storageReference.downloadUrl.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val imageData = task.result
                                                val chatItem = Chat(
                                                    imageData,
                                                    name ?: "",
                                                    uid ?: "",
                                                    true
                                                )
                                                chatItems.add(chatItem)
                                                // 아마 data.size가 채팅방 개수이지 않을까?
                                                Log.e(
                                                    "size",
                                                    "${chatItems.size} / ${chatRoomArray.size}"
                                                )
                                                if (chatItems.size == chatRoomArray.size) {
                                                    for (item in chatItems) adapter.addChat(item)
                                                    adapter.notifyDataSetChanged()
                                                    Log.e("chat", "good")
                                                }
                                            } else Log.e("downloadUrl", "failed")
                                        }
                                    }
                            }
                        }
                    }
                }
        }
        listview.setOnItemClickListener { parent, view, position, id ->
            // 클릭된 Chat 아이템을 어댑터로부터 가져옵니다
            val chatItem = adapter.getItem(position) as Chat

            // 이제, Intent를 사용하여 다음 액티비티로 필요한 데이터(예: chatItem의 uid)를 전달합니다
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("uid", chatItem.uid)
            intent.putExtra("name",chatItem.name)
            // 다음 액티비티로 전달해야 할 다른 데이터가 있다면 intent.putExtra()를 사용하여 추가합니다
            startActivity(intent)
        }
    }

}

