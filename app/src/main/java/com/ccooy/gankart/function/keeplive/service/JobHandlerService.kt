package com.ccooy.gankart.function.keeplive.service

import android.app.ActivityManager
import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.ccooy.gankart.function.keeplive.KeepLive
import com.ccooy.gankart.function.keeplive.config.NotificationUtils
import com.ccooy.gankart.function.keeplive.receiver.NotificationClickReceiver

@SuppressWarnings(value = ["unchecked", "deprecation"])
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class JobHandlerService : JobService() {

    private var mJobScheduler: JobScheduler? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var startId = startId
        startService(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mJobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val builder = JobInfo.Builder(startId++,
                    ComponentName(packageName, JobHandlerService::class.java.name))
            if (Build.VERSION.SDK_INT >= 24) {
                builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS) //执行的最小延迟时间
                builder.setOverrideDeadline(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS)  //执行的最长延时时间
                builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS)
                builder.setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR)//线性重试方案
            } else {
                builder.setPeriodic(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS)
            }
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            builder.setRequiresCharging(true) // 当插入充电器，执行该任务
            mJobScheduler?.schedule(builder.build())
        }
        return Service.START_STICKY
    }

    private fun startService(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (KeepLive.foregroundNotification != null) {
                val intent = Intent(applicationContext, NotificationClickReceiver::class.java)
                intent.action = NotificationClickReceiver.CLICK_NOTIFICATION
                val notification = NotificationUtils.createNotification(this, KeepLive.foregroundNotification!!.getTitle(), KeepLive.foregroundNotification!!.getDescription(), KeepLive.foregroundNotification!!.getIconRes(), intent)
                startForeground(13691, notification)
            }
        }
        //启动本地服务
        val localIntent = Intent(context, LocalService::class.java)
        //启动守护进程
        val guardIntent = Intent(context, RemoteService::class.java)
        startService(localIntent)
        startService(guardIntent)
    }

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        if (!isServiceRunning(applicationContext, "com.xiyang51.keeplive.service.LocalService") || !isServiceRunning(applicationContext, "$packageName:remote")) {
            startService(this)
        }
        return false
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        if (!isServiceRunning(applicationContext, "com.xiyang51.keeplive.service.LocalService") || !isServiceRunning(applicationContext, "$packageName:remote")) {
            startService(this)
        }
        return false
    }

    private fun isServiceRunning(ctx: Context, className: String): Boolean {
        var isRunning = false
        val activityManager = ctx
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val servicesList = activityManager
                .getRunningServices(Integer.MAX_VALUE)
        val l = servicesList.iterator()
        while (l.hasNext()) {
            val si = l.next()
            if (className == si.service.className) {
                isRunning = true
            }
        }
        return isRunning
    }
}