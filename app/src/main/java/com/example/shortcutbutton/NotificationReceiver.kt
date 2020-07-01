package com.example.shortcutbutton

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, dataIntent: Intent?) {
        Log.e("onReceive", "context $context")
        context.startService(Intent(context, LockService::class.java))
    }
}