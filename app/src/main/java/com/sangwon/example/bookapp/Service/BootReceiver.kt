package com.sangwon.example.bookapp.Service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Intent(context, MessageNotificationService::class.java).also { intent ->
            context.startService(intent)
        }
    }
}