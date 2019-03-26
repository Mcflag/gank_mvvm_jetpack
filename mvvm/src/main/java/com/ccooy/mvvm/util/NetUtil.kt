@file:Suppress("NOTHING_TO_INLINE")

package com.ccooy.mvvm.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager

/**
 * 判断网络状态是否可用
 */
fun Context.isNetworkAvailable(): Boolean = applicationContext.isNetworkAvailable()

fun Application.isNetworkAvailable(): Boolean =
    (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo.isAvailable