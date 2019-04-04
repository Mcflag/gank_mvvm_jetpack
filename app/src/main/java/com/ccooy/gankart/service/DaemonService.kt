package com.ccooy.gankart.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.ccooy.gankart.R
import timber.log.Timber

/**前台Service，使用startForeground
 * 这个Service尽量要轻，不要占用过多的系统资源，否则
 * 系统在资源紧张时，照样会将其杀死
 */
class DaemonService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Timber.tag(TAG).d("DaemonService---->onCreate被调用，启动前台service")
        //如果API大于18，需要弹出一个可见通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val builder = Notification.Builder(this)
            builder.setSmallIcon(R.mipmap.ic_launcher)
            builder.setContentTitle("KeepAppAlive")
            builder.setContentText("DaemonService is runing...")
            startForeground(NOTICE_ID, builder.build())
            // 如果觉得常驻通知栏体验不好
            // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
            val intent = Intent(this, CancelNoticeService::class.java)
            startService(intent)
        } else {
            startForeground(NOTICE_ID, Notification())
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // 如果Service被终止
        // 当资源允许情况下，重启service
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // 如果Service被杀死，干掉通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val mManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mManager.cancel(NOTICE_ID)
        }
        Timber.tag(TAG).d("DaemonService---->onDestroy，前台service被杀死")
        // 重启自己
        val intent = Intent(getApplicationContext(), DaemonService::class.java)
        startService(intent)
    }

    companion object {
        private val TAG = "DaemonService"
        val NOTICE_ID = 100
    }
}