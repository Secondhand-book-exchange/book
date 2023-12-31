package com.sangwon.example.bookapp.Service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sangwon.example.bookapp.ChatListActivity
import com.sangwon.example.bookapp.R

class MessageNotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        onHandleIntent()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun onHandleIntent() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance().currentUser
        db.collection("users").document(auth?.uid.toString()).addSnapshotListener { user, e ->
            val chatRoomArray = user?.get("ChatRoom") as ArrayList<String>?

            if (!chatRoomArray.isNullOrEmpty()) {
                val chatRoomMap = HashMap<String, String>()
                for (senderRoom in chatRoomArray)
                    db.collection("Chats").document(auth?.uid.toString())
                        .collection(senderRoom)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener { snapshot->
                            if (snapshot != null && !snapshot.isEmpty)
                                for (document in snapshot.documents) {
                                    val message = document.getString("message")
                                    Log.e("FIRSTMESSAGE" ,message.toString())
                                    if (message != null && document.getString("sendId") != auth?.uid) {
                                        chatRoomMap[senderRoom] = message
                                        break
                                    }
                                }
                        }

                Thread.sleep(1000)

                for (senderRoom in chatRoomArray)
                    db.collection("Chats").document(auth?.uid.toString())
                        .collection(senderRoom)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                Log.e("MessageError", e.message.toString())
                            } else if (snapshot != null && !snapshot.isEmpty) {
                                for (document in snapshot.documents) {
                                    val id = document.getString("sendId")
                                    if (id != auth?.uid) {
                                        val message = document.getString("message")
                                        if (message != null && message != chatRoomMap[senderRoom]) {
                                            db.collection("users").document(id!!).get().addOnSuccessListener {
                                                val name = it.getString("name")?:"알 수 없음"
                                                notice(
                                                    senderRoom,
                                                    name,
                                                    message
                                                )
                                                stackChatLog(message, name)
                                                Log.e("qqqqqqqqq","${chatRoomMap[senderRoom]} $message")
                                                chatRoomMap[senderRoom] = message
                                            }
                                        }
                                    }
                                    break
                                }
                            }
                        }
            }
        }
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

    private fun stackChatLog(message: String, sender:String){
        val log = "${sender}님이 \"$message\"라고 메시지를 보냈습니다."

    }
}