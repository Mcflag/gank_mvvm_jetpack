package com.ccooy.gankart.function.keeplive.service

import android.app.Service
import android.content.*
import android.media.MediaPlayer
import android.os.*
import com.ccooy.gankart.R
import com.ccooy.gankart.function.keeplive.KeepLive
import com.ccooy.gankart.function.keeplive.config.NotificationUtils
import com.ccooy.gankart.function.keeplive.receiver.NotificationClickReceiver

class LocalService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var mBilder: MyBilder? = null

    override fun onCreate() {
        super.onCreate()
        if (mBilder == null) {
            mBilder = MyBilder()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBilder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        //播放无声音乐
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.novioce)
            //声音设置为0
            mediaPlayer?.setVolume(0f, 0f)
            mediaPlayer?.isLooping = true//循环播放
            play()
        }
        //启用前台服务，提升优先级
        if (KeepLive.foregroundNotification != null) {
            val intent2 = Intent(applicationContext, NotificationClickReceiver::class.java)
            intent2.action = NotificationClickReceiver.CLICK_NOTIFICATION
            val notification = NotificationUtils.createNotification(
                this,
                KeepLive.foregroundNotification!!.getTitle(),
                KeepLive.foregroundNotification!!.getDescription(),
                KeepLive.foregroundNotification!!.getIconRes(),
                intent2
            )
            startForeground(13691, notification)
        }
        //绑定守护进程
        try {
            val intent3 = Intent(this, RemoteService::class.java)
            this.bindService(intent3, connection, Context.BIND_ABOVE_CLIENT)
        } catch (e: Exception) {
        }

        //隐藏服务通知
        try {
            if (Build.VERSION.SDK_INT < 25) {
                startService(Intent(this, HideForegroundService::class.java))
            }
        } catch (e: Exception) {
        }

        if (KeepLive.keepLiveService != null) {
            KeepLive.keepLiveService!!.onWorking()
        }
        return Service.START_STICKY
    }

    private fun play() {
        if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
        }
    }

    private inner class MyBilder : GuardAidl.Stub() {

        @Throws(RemoteException::class)
        override fun wakeUp(title: String, discription: String, iconRes: Int) {

        }
    }

    private val connection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            val remoteService = Intent(
                this@LocalService,
                RemoteService::class.java
            )
            this@LocalService.startService(remoteService)
            val intent = Intent(this@LocalService, RemoteService::class.java)
            this@LocalService.bindService(
                intent, this,
                Context.BIND_ABOVE_CLIENT
            )
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            try {
                if (mBilder != null && KeepLive.foregroundNotification != null) {
                    val guardAidl = GuardAidl.Stub.asInterface(service)
                    guardAidl.wakeUp(
                        KeepLive.foregroundNotification?.getTitle(),
                        KeepLive.foregroundNotification?.getDescription(),
                        KeepLive.foregroundNotification!!.getIconRes()
                    )
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        if (KeepLive.keepLiveService != null) {
            KeepLive.keepLiveService?.onStop()
        }
    }
}