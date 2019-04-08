package com.ccooy.gankart.function.keeplive.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ccooy.gankart.function.keeplive.KeepLive

/**
 * 通知栏点击广播接受者
 */
class NotificationClickReceiver : BroadcastReceiver() {

    companion object {
        const val CLICK_NOTIFICATION = "CLICK_NOTIFICATION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == NotificationClickReceiver.CLICK_NOTIFICATION) {
            if (KeepLive.foregroundNotification != null) {
                if (KeepLive.foregroundNotification!!.getForegroundNotificationClickListener() != null) {
                    KeepLive.foregroundNotification!!.getForegroundNotificationClickListener()
                        ?.foregroundNotificationClick(context, intent)
                }
            }
        }
    }
}