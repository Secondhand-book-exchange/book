package com.sangwon.example.bookapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    private lateinit var listview: ListView
    private lateinit var adapter: ChatListAdapter
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var imagePath: String
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
                            var loadedCount = 0

                            for (chatroom in chatRoomArray) {
                                val uid = chatroom.substring(0, 28)

                                db.collection("users").document("$uid")
                                    .get().addOnSuccessListener { documentSnapshot ->
                                        val name = documentSnapshot.getString("name")

                                        imagePath = "profile_images/$uid"
                                        val storageReference =
                                            firebaseStorage.getReference().child(imagePath)
                                        storageReference.downloadUrl.addOnCompleteListener { task ->
                                            lateinit var imageData: Uri
                                            if (task.isSuccessful) {
                                                imageData = task.result
                                            } else {
                                                imageData = Uri.parse(defaultImage)
                                            }
                                            val chatItem = Chat(
                                                imageData,
                                                name ?: "",
                                                uid,
                                                true
                                            )
                                            chatItems.add(chatItem)

                                            loadedCount++

                                            if (loadedCount == chatRoomArray.size) {
                                                if (chatItems.isEmpty()) {
                                                    val emptyChatItem = Chat(
                                                        Uri.parse(defaultImage),
                                                        "채팅창이 없습니다.",
                                                        "",
                                                        true
                                                    )
                                                    chatItems.add(emptyChatItem)
                                                }
                                                for (item in chatItems) adapter.addChat(item)
                                                adapter.notifyDataSetChanged()
                                            }
                                        }
                                    }
                            }
                        } else {
                            val emptyChatItem = Chat(
                                Uri.parse(defaultImage),
                                "채팅창이 없습니다.",
                                "",
                                false
                            )
                            chatItems.add(emptyChatItem)
                            adapter.addChat(emptyChatItem)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
        }
        listview.setOnItemClickListener { parent, view, position, id ->
            val chatItem = adapter.getItem(position) as Chat
            if (chatItem.uid.isNotEmpty()) {
                chatItem.check = false
                adapter.notifyDataSetChanged()

                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("uid", chatItem.uid)
                intent.putExtra("name", chatItem.name)
                startActivity(intent)
            }
        }
    }
}
