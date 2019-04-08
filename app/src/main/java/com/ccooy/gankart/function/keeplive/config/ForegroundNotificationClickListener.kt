package com.ccooy.gankart.function.keeplive.config

import android.content.Context
import android.content.Intent

/**
 * 通知栏点击回调接口
 */
interface ForegroundNotificationClickListener {
    fun foregroundNotificationClick(context: Context, intent: Intent)
}