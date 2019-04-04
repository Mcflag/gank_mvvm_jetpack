package com.ccooy.gankart.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import com.ccooy.gankart.R

/**
 * 移除前台Service通知栏标志，这个Service选择性使用
 */
class CancelNoticeService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val builder = Notification.Builder(this)
            builder.setSmallIcon(R.mipmap.ic_launcher)
            startForeground(DaemonService.NOTICE_ID, builder.build())
            // 开启一条线程，去移除DaemonService弹出的通知
            Thread(Runnable {
                // 延迟1s
                SystemClock.sleep(1000)
                // 取消CancelNoticeService的前台
                stopForeground(true)
                // 移除DaemonService弹出的通知
                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(DaemonService.NOTICE_ID)
                // 任务完成，终止自己
                stopSelf()
            }).start()
        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}