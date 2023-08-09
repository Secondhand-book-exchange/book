package com.sangwon.example.bookapp

import android.content.Intent
import android.net.Uri
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
import java.sql.Timestamp

class ChatListActivity : AppCompatActivity() {
    private lateinit var listview: ListView
    private lateinit var adapter: ChatListAdapter
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var imagePath: String
    private var check:Boolean = true
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val defaultImage = "/drawable/profile.jpg"

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

            db.collection("users").document("${auth.currentUser?.uid}")
                .get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val chatRoomArray = documentSnapshot.get("ChatRoom") as ArrayList<String>?

                        if (!chatRoomArray.isNullOrEmpty()) {
                            for (chatroom in chatRoomArray) {
                                val uid = chatroom.substring(0, 28)

                                db.collection("users").document("$uid")
                                    .get().addOnSuccessListener { documentSnapshot ->
                                        val name = documentSnapshot.getString("name")

                                        imagePath = "profile_images/$uid"

                                        val storageReference =
                                            firebaseStorage.getReference().child(imagePath)
                                        storageReference.downloadUrl.addOnCompleteListener { task ->
                                            lateinit var imageData:Uri


                                            if (task.isSuccessful) {
                                                imageData = task.result
                                            } else {
                                                imageData = Uri.parse(defaultImage)
                                            }
                                            val senderUid = auth.currentUser?.uid

                                            db.collection("Chats").document(uid)
                                                .collection("${senderUid + uid}").document("ChatingRoomInfo").get()
                                                .addOnSuccessListener { documentSnapshot ->
                                                    check = documentSnapshot.getBoolean("check")!!
                                                    val timestamp = documentSnapshot.getString("timestamp")
                                                    Log.e("check","${check}")
                                                    val chatItem = Chat(
                                                        imageData,
                                                        name ?: "",
                                                        uid,
                                                        check,
                                                        timestamp ?: ""
                                                    )
                                                    chatItems.add(chatItem)

                                                    if (chatItems.size == chatRoomArray.size) {
                                                        chatItems.sortByDescending { it.timestamp }
                                                        for (item in chatItems) adapter.addChat(item)
                                                        adapter.notifyDataSetChanged()
                                                    }
                                                }


                                        }
                                    }
                            }
                        } else {
                            val emptyChatItem = Chat(
                                Uri.parse(defaultImage),
                                "채팅창이 없습니다.",
                                "",
                                false,
                                "0"
                            )
                            chatItems.add(emptyChatItem)
                            adapter.addChat(emptyChatItem)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
        }

        listview.setOnItemClickListener { parent, view, position, id ->
            // 클릭된 Chat 아이템을 어댑터로부터 가져옵니다

            val chatItem = adapter.getItem(position) as Chat
            val senderUid = auth.currentUser?.uid
            val receiverUid = chatItem.uid
            val senderRoom = receiverUid + senderUid
            val receiverRoom = senderUid + receiverUid

            if(chatItem.timestamp == "0") {
                return@setOnItemClickListener
            }
            chatItem.check = false

            //클릭 시 양쪽의 ChatingRoomInfo에 접근하여 check를 false로 바꾸기 아니지 한쪽만 해야지
            db.collection("Chats").document(receiverUid!!)
                .collection("${receiverRoom}").document("ChatingRoomInfo").update("check",false)
                .addOnSuccessListener {
                    Log.e("receuverUid","${receiverUid}")
                    Log.e("receuverRoom","${receiverRoom}")

                }


            adapter.notifyDataSetChanged()


            // 이제, Intent를 사용하여 다음 액티비티로 필요한 데이터(예: chatItem의 uid)를 전달합니다
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("uid", chatItem.uid)
            intent.putExtra("name",chatItem.name)
            // 다음 액티비티로 전달해야 할 다른 데이터가 있다면 intent.putExtra()를 사용하여 추가합니다
            startActivity(intent)
        }
    }
}
