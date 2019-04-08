package com.ccooy.gankart.base

import android.app.Application
import android.content.Context
import android.content.Intent
import com.ccooy.gankart.BuildConfig
import com.ccooy.gankart.R
import com.ccooy.gankart.di.dbModule
import com.ccooy.gankart.di.httpClientModule
import com.ccooy.gankart.di.prefsModule
import com.ccooy.gankart.di.serviceModule
import com.ccooy.gankart.function.keeplive.KeepLive
import com.ccooy.gankart.function.keeplive.config.ForegroundNotification
import com.ccooy.gankart.function.keeplive.config.ForegroundNotificationClickListener
import com.ccooy.gankart.function.keeplive.config.KeepLiveService
import com.ccooy.mvvm.logger.initLogger
import com.squareup.leakcanary.LeakCanary
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidModule
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import timber.log.Timber
import java.util.*


open class BaseApplication : Application(), KodeinAware {

    override val kodein: Kodein = Kodein.lazy {
        bind<Context>() with singleton { this@BaseApplication }
        import(androidModule(this@BaseApplication))
        import(androidXModule(this@BaseApplication))

        import(serviceModule)
        import(dbModule)
        import(httpClientModule)
        import(prefsModule)
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        initLogger(BuildConfig.DEBUG)
        initLeakCanary()
        initKeepLive()
    }

    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    private fun initKeepLive() {
        KeepLive.startWork(this, KeepLive.RunMode.ROGUE, ForegroundNotification("Title", "message",
            R.mipmap.ic_github, object : ForegroundNotificationClickListener {
                override fun foregroundNotificationClick(context: Context, intent: Intent) {
                    //点击通知回调

                }
            }), object : KeepLiveService {

            var timer: Timer = Timer()
            var task: TimerTask = object : TimerTask() {
                override fun run() {
                    Timber.tag("KEEPLIVE").d("正在执行" + System.currentTimeMillis())
                }
            }

            override fun onStop() {
                //可能调用多次，跟onWorking匹配调用
                Timber.tag("KEEPLIVE").d("停止服务")
            }

            override fun onWorking() {
                //一直存活，可能调用多次
                Timber.tag("KEEPLIVE").d("开始服务")
                task?.let {
                    task.cancel()
                    task = object : TimerTask() {
                        override fun run() {
                            Timber.tag("KEEPLIVE").d("正在执行" + System.currentTimeMillis())
                        }
                    }
                }
                timer.schedule(task, 0, 10000)
            }
        })
    }

    companion object {
        lateinit var INSTANCE: BaseApplication
    }
}