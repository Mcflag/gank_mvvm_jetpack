package com.ccooy.gankart.function.keeplive.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity

/**
 * 1像素Activity
 */
class OnePixelActivity : AppCompatActivity() {

    private lateinit var br: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设定一像素的activity
        val window = window
        window.setGravity(Gravity.LEFT or Gravity.TOP)
        val params = window.attributes
        params.x = 0
        params.y = 0
        params.height = 1
        params.width = 1
        window.attributes = params
        //在一像素activity里注册广播接受者    接受到广播结束掉一像素
        br = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                finish()
            }
        }
        registerReceiver(br, IntentFilter("finish activity"))
        checkScreenOn()
    }

    override fun onResume() {
        super.onResume()
        checkScreenOn()
    }

    override fun onDestroy() {
        try {
            //销毁的时候解锁广播
            unregisterReceiver(br)
        } catch (e: IllegalArgumentException) {
        }
        super.onDestroy()
    }

    /**
     * 检查屏幕是否点亮
     */
    private fun checkScreenOn() {
        val pm = this@OnePixelActivity.getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            pm.isInteractive
        } else {
            pm.isScreenOn
        }
        if (isScreenOn) {
            finish()
        }
    }
}