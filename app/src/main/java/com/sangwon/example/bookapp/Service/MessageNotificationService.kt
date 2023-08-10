package com.sangwon.example.bookapp.Service

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.sangwon.example.bookapp.ChatListActivity
import com.sangwon.example.bookapp.MainActivity
import com.sangwon.example.bookapp.R

class MessageNotificationService : IntentService(MessageNotificationService::class.simpleName) {
    private lateinit var messageListener: ListenerRegistration

    override fun onHandleIntent(intent: Intent?) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance().currentUser
        db.collection("users").document(auth?.uid.toString()).get().addOnSuccessListener { user ->
            val chatRoomArray = user.get("ChatRoom") as ArrayList<String>?

            if (!chatRoomArray.isNullOrEmpty()) {
                val chatRoomMap = HashMap<String, String>()
                for (senderRoom in chatRoomArray) {
                    db.collection("Chats").document(auth?.uid.toString())
                        .collection(senderRoom)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, e ->
                            if (snapshot != null && !snapshot.isEmpty)
                                chatRoomMap[senderRoom] =
                                    snapshot.documents[0].getString("message") ?: ""
                        }
                }

                for (senderRoom in chatRoomArray)
                    db.collection("Chats").document(auth?.uid.toString())
                        .collection(senderRoom)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                Log.e("MessageError", e.message.toString())
                            } else if (snapshot != null && !snapshot.isEmpty) {
                                for (document in snapshot.documents) {
                                    if (document.getString("sendId") != auth?.uid) {
                                        val message = document.getString("message")
                                        if (message != null) {
                                            notice(
                                                senderRoom,
                                                document.getString("sendId")!!,
                                                message
                                            )
                                            chatRoomMap[senderRoom] = message
                                        }
                                        break
                                    }
                                }
                            }
                        }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    private fun notice(channelId: String, channelName: String, message: String) {
// NotificationManager 객체 생성
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Notification Channel 아이디, 이름, 설명, 중요도 설정
        val channelDescription = "첫 번째 채널에 대한 설명입니다."
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        // NotificationChannel 객체 생성
        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        // 설명 설정
        notificationChannel.description = channelDescription

        // 채널에 대한 각종 설정(불빛, 진동 등)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern = longArrayOf(100L, 200L, 300L)
        // 시스템에 notificationChannel 등록
        notificationManager.createNotificationChannel(notificationChannel)

        val notificationCompatBuilder = NotificationCompat.Builder(this, channelId)

        val intent = Intent(this, ChatListActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        notificationCompatBuilder.let {
            // 작은 아이콘 설정
            it.setSmallIcon(R.mipmap.ic_launcher)
            // 시간 설정
            it.setWhen(System.currentTimeMillis())
            // 알림 메시지 설정
            it.setContentTitle("채팅 왓숑")
            // 알림 내용 설정
            it.setContentText(message)

            it.setContentIntent(pendingIntent)
            // 알림과 동시에 진동 설정(권한 필요(
            it.setDefaults(Notification.DEFAULT_VIBRATE)
            // 클릭 시 알림이 삭제되도록 설정
            it.setAutoCancel(true)
        }

        notificationManager.notify(0, notificationCompatBuilder.build())
    }

    fun stackChatLog(message: String, sender:String){

    }
}