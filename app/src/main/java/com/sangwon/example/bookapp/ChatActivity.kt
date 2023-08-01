package com.sangwon.example.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sangwon.example.bookapp.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var receiverName: String
    private lateinit var receiverUid: String
    private lateinit var binding: ActivityChatBinding

    private lateinit var receiverRoom: String //받는 대화방
    private lateinit var senderRoom: String //보낸 대화방

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //넘어온 데이터 변수에 담기
//        receiverName = intent.getStringExtra("name").toString()
//        receiverUid = intent.getStringExtra("uid").toString()

        receiverName = "dondoncham"
        receiverUid = "PAzVTYJY5fSXbxXHEFWHM5MC9bN2"

        var auth = Firebase.auth // 인증
        var db = Firebase.firestore // DB 객체

        //접속자 uid
        val senderUid = auth.currentUser?.uid

        //보낸이 방
        senderRoom = receiverUid + senderUid

        //받는이방
        receiverRoom = senderUid + receiverUid

        //액션바에 상대방 이름 보여주기
        supportActionBar?.title = receiverName

        //메시지 전송 버튼 이벤트
        binding.sendButton.setOnClickListener{
            Log.e("BBB","Clicked")
            val message = binding.messageEdit.text.toString()
            val messageObject = Message(message, senderUid)
            db.collection("Chats").document(senderUid!!)
                .collection("${senderRoom}").document().set(messageObject)
                .addOnSuccessListener {
                    db.collection("Chats").document(receiverUid)
                        .collection("${receiverRoom}").document().set(messageObject)
                }
        }

    }
}