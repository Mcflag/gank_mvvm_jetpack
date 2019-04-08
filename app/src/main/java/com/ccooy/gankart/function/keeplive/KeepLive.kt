package com.ccooy.gankart.function.keeplive

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.NonNull
import com.ccooy.gankart.function.keeplive.config.ForegroundNotification
import com.ccooy.gankart.function.keeplive.config.KeepLiveService
import com.ccooy.gankart.function.keeplive.service.JobHandlerService
import com.ccooy.gankart.function.keeplive.service.LocalService
import com.ccooy.gankart.function.keeplive.service.RemoteService

object KeepLive {

    /**
     * 运行模式
     */
    enum class RunMode {
        /**
         * 省电模式
         * 省电一些，但保活效果会差一点
         */
        ENERGY,
        /**
         * 流氓模式
         * 相对耗电，但可造就不死之身
         */
        ROGUE
    }

    var foregroundNotification: ForegroundNotification? = null
    var keepLiveService: KeepLiveService? = null
    var runMode: RunMode? = null

    fun startWork(
        @NonNull application: Application, @NonNull runMode: RunMode, @NonNull foregroundNotification: ForegroundNotification,
        keepLiveService: KeepLiveService
    ) {
        if (isMain(application)) {
            KeepLive.foregroundNotification = foregroundNotification
            KeepLive.keepLiveService = keepLiveService
            KeepLive.runMode = runMode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //启动定时器，在定时器中启动本地服务和守护进程
                val intent = Intent(application, JobHandlerService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    application.startForegroundService(intent)
                } else {
                    application.startService(intent)
                }
            } else {
                //启动本地服务
                val localIntent = Intent(application, LocalService::class.java)
                //启动守护进程
                val guardIntent = Intent(application, RemoteService::class.java)
                application.startService(localIntent)
                application.startService(guardIntent)
            }
        }
    }

    private fun isMain(app: Application): Boolean {
        val pid = android.os.Process.myPid()
        var processName = ""
        val mActivityManager = app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (appProcess in mActivityManager.runningAppProcesses) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName
                break
            }
        }
        val packageName = app.packageName
        return processName == packageName
    }
}

