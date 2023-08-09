package com.sangwon.example.bookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sangwon.example.bookapp.Adapter.MessageAdapter
import com.sangwon.example.bookapp.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var receiverName: String
    private lateinit var receiverUid: String
    private lateinit var binding: ActivityChatBinding

    private lateinit var receiverRoom: String //받는 대화방
    private lateinit var senderRoom: String //보낸 대화방

    private lateinit var messageList: ArrayList<Message>
    //Listener가 액티비티 종료 후에도 동작하는 것을 막기 위해 삭제 해주기
    private lateinit var messageListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //초기화
        messageList = ArrayList()
        val messageAdapter: MessageAdapter = MessageAdapter(this, messageList)

        //RecyclerView
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter

        //넘어온 데이터 변수에 담기
        receiverName = intent.getStringExtra("name").toString()
        receiverUid = intent.getStringExtra("uid").toString()

        var auth = Firebase.auth // 인증
        var db = Firebase.firestore // DB 객체

        //접속자 uid
        val senderUid = auth.currentUser?.uid

        //보낸이 방
        senderRoom = receiverUid + senderUid

        //받는이 방
        receiverRoom = senderUid + receiverUid

        //users 정보에 채팅방 정보 추가
        val userDocumentRef = db.collection("users").document("${auth.currentUser?.uid}")
        userDocumentRef.update("ChatRoom", FieldValue.arrayUnion(senderRoom))
            .addOnSuccessListener {
                db.collection("users").document(receiverUid)
                    .update("ChatRoom", FieldValue.arrayUnion(receiverRoom))
            }
            .addOnFailureListener {
                Log.e("Fire","배열 필드 업테이드 실패")
            }
        val chatRoomInfoInit = ChatingRoomInfo(true, Timestamp.now().toString())
        //ChatingRoomInfo 초기화:양쪽 방의 상태를 초기화
        db.collection("Chats").document(senderUid!!)
            .collection(senderRoom).document("ChatingRoomInfo").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentSnapshot = task.result
                    if (!documentSnapshot.exists()) {
                        // ChatingRoomInfo 문서가 존재하지 않는 경우 초기화
                        db.collection("Chats").document(senderUid!!)
                            .collection(senderRoom)
                            .document("ChatingRoomInfo")
                            .set(chatRoomInfoInit)
                            .addOnSuccessListener {
                                db.collection("Chats").document(receiverUid!!)
                                    .collection(receiverRoom).document("ChatingRoomInfo").set(chatRoomInfoInit)
                            }
                    }
                } else {
                    Log.e("ChatListActivity", "Error getting ChatingRoomInfo document: ", task.exception)
                }
            }

        //액션바에 상대방 이름 보여주기
        supportActionBar?.title = receiverName

        //메시지 전송 버튼 이벤트
        binding.sendButton.setOnClickListener{
            val message = binding.messageEdit.text.toString()
            val timestamp = Timestamp.now().toString()
            val messageObject = Message(message, senderUid, timestamp)

            //채팅방 정보 생성
            val chatRoomInfo = ChatingRoomInfo(true, Timestamp.now().toString())

            // 데이터 저장
            db.collection("Chats").document(senderUid!!)
                .collection("${senderRoom}").document().set(messageObject)
                .addOnSuccessListener {
                    db.collection("Chats").document(receiverUid)
                        .collection("${receiverRoom}").document().set(messageObject)
                }

            //채팅방 정보 저장
            db.collection("Chats").document(senderUid!!)
                .collection("${senderRoom}").document("ChatingRoomInfo").set(chatRoomInfo)
//                .addOnSuccessListener {
////                    db.collection("Chats").document(receiverUid)
////                        .collection("${receiverRoom}").document("ChatingRoomInfo").set(ChatingRoomInfo)
//                }


            //입력값 초기화
            binding.messageEdit.setText("")
        }

        //메시지 가져오기
        messageListener = db.collection("Chats").document(senderUid!!)
            .collection("${senderRoom}")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null && !snapshot.isEmpty) {
                   messageList.clear()

                    for(document in snapshot.documents){
                        if(document.id != "ChatingRoomInfo") {
                            val message = document.getString("message")
                            val senderUid = document.getString("sendId")
                            val timestamp = document.getString("timestamp")
                            val messageObject = Message(message, senderUid, timestamp!!)
                            messageList.add(messageObject)
                        }
                        scrollRecyclerViewToBottom()

                    }
                    messageAdapter.notifyDataSetChanged()

                } else {
                    Log.e("snapshoterror", "Error getting messages: $e")
                }
            }
    }
    override fun onDestroy() {
        super.onDestroy()
        messageListener.remove()
    }

    private fun scrollRecyclerViewToBottom() {
        binding.chatRecyclerView.scrollToPosition(messageList.size - 1)
    }
}