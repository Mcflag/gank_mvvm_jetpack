package com.ccooy.gankart.service

import android.content.Intent
import android.app.Service
import android.media.MediaPlayer
import android.os.IBinder
import com.ccooy.gankart.R
import timber.log.Timber


class PlayerMusicService : Service() {
    private var mMediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Timber.tag(TAG).d("$TAG---->onCreate,启动服务")
        mMediaPlayer = MediaPlayer.create(applicationContext, R.raw.no_kill)
        mMediaPlayer!!.isLooping = true
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Thread(Runnable { startPlayMusic() }).start()
        return START_STICKY
    }

    private fun startPlayMusic() {
        if (mMediaPlayer != null) {
            Timber.tag(TAG).d("启动后台播放音乐")
            mMediaPlayer!!.start()
        }
    }

    private fun stopPlayMusic() {
        if (mMediaPlayer != null) {
            Timber.tag(TAG).d("关闭后台播放音乐")
            mMediaPlayer!!.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayMusic()
        Timber.tag(TAG).d("$TAG---->onCreate,停止服务")
        // 重启
        val intent = Intent(applicationContext, PlayerMusicService::class.java)
        startService(intent)
    }

    companion object {
        private val TAG = "PlayerMusicService"
    }
}
