package com.ccooy.gankart.service

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import timber.log.Timber

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class KeepJobService : JobService() {
    private var jobId: Int = 0

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        var mStartId = startId
        Timber.tag(TAG).d("keep-> onStartCommand: job")
        val mJobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        mJobScheduler.cancel(jobId)
        val builder = JobInfo.Builder(
            mStartId++,
            ComponentName(packageName, KeepJobService::class.java.name)
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //执行的最小延迟时间
            builder.setMinimumLatency(PERIODIC_TIME)
            //执行的最长延时时间
            builder.setOverrideDeadline(PERIODIC_TIME)
            builder.setMinimumLatency(PERIODIC_TIME)
            //线性重试方案
            builder.setBackoffCriteria(PERIODIC_TIME, JobInfo.BACKOFF_POLICY_LINEAR)
        } else {
            //每隔5秒运行一次
            builder.setPeriodic(PERIODIC_TIME)
            builder.setRequiresDeviceIdle(true)
        }
        builder.setRequiresCharging(true)
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        //设置设备重启后，是否重新执行任务
        builder.setPersisted(true)
        jobId = mJobScheduler.schedule(builder.build())
        if (jobId <= 0) {
            Timber.tag(TAG).d("keep-> schedule failed")
        } else {
            Timber.tag(TAG).d("keep-> schedule success")
        }

        return START_STICKY
    }

    override fun onStartJob(params: JobParameters): Boolean {
        Timber.tag(TAG).d("keep-> onStartJob")
        //启动双进程的服务
        return false
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return false
    }

    companion object {
        private const val TAG = "KeepJobService"
        /**
         *
         */
        const val PERIODIC_TIME: Long = 5 * 60 * 1000
    }

}