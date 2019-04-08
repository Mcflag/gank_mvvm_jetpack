package com.ccooy.gankart.function.keeplive.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.ccooy.gankart.function.keeplive.KeepLive
import com.ccooy.gankart.function.keeplive.config.NotificationUtils
import com.ccooy.gankart.function.keeplive.receiver.NotificationClickReceiver

class HideForegroundService : Service() {

    private var handler: android.os.Handler? = null
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground()
        if (handler == null) {
            handler = Handler()
        }
        handler!!.postDelayed({
            stopForeground(true)
            stopSelf()
        }, 2000)
        return Service.START_NOT_STICKY
    }


    private fun startForeground() {
        if (KeepLive.foregroundNotification != null) {
            val intent = Intent(applicationContext, NotificationClickReceiver::class.java)
            intent.action = NotificationClickReceiver.CLICK_NOTIFICATION
            val notification = NotificationUtils.createNotification(this, KeepLive.foregroundNotification!!.getTitle(), KeepLive.foregroundNotification!!.getDescription(), KeepLive.foregroundNotification!!.getIconRes(), intent)
            startForeground(13691, notification)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}