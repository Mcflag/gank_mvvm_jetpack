package com.ccooy.gankart.function.keeplive.config

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class NotificationUtils(context: Context) : ContextWrapper(context) {

    private var manager: NotificationManager? = null
    private var id: String = context.packageName + "51"
    private var name: String = context.packageName
    private var context: Context = context
    private var channel: NotificationChannel? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var notificationUtils: NotificationUtils? = null

        fun createNotification(
            context: Context,
            title: String,
            content: String,
            icon: Int,
            intent: Intent
        ): Notification? {
            if (notificationUtils == null) {
                notificationUtils = NotificationUtils(context)
            }
            var notification: Notification? = null
            notification = if (Build.VERSION.SDK_INT >= 26) {
                notificationUtils?.createNotificationChannel()
                notificationUtils?.getChannelNotification(title, content, icon, intent)?.build()
            } else {
                notificationUtils?.getNotification_25(title, content, icon, intent)?.build()
            }
            return notification
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        if (channel == null) {
            channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_MIN)
            channel?.enableLights(false)
            channel?.enableVibration(false)
            channel?.vibrationPattern = longArrayOf(0)
            channel?.setSound(null, null)
            getManager().createNotificationChannel(channel)
        }
    }

    private fun getManager(): NotificationManager {
        if (manager == null) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager!!
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getChannelNotification(title: String, content: String, icon: Int, intent: Intent): Notification.Builder {
        //PendingIntent.FLAG_UPDATE_CURRENT 这个类型才能传值
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return Notification.Builder(context, id)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(icon)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    }

    fun getNotification_25(title: String, content: String, icon: Int, intent: Intent): NotificationCompat.Builder {
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(context, id)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(icon)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0))
            .setSound(null)
            .setLights(0, 0, 0)
            .setContentIntent(pendingIntent)
    }
}

