package com.ccooy.gankart.ui.splash

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton

const val SPLASH_MODULE_TAG = "SPLASH_MODULE_TAG"

val splashKodeinModule = Kodein.Module(SPLASH_MODULE_TAG) {

    bind<SplashViewModel>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        ViewModelProviders
            .of(context, SplashViewModelFactory.getInstance(instance()))
            .get(SplashViewModel::class.java)
    }

    bind<SplashLocalDataSource>() with singleton {
        SplashLocalDataSource()
    }

    bind<SplashDataSourceRepository>() with singleton {
        SplashDataSourceRepository(instance())
    }
}