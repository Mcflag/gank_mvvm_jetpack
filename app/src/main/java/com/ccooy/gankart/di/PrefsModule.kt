package com.ccooy.gankart.di

import android.content.Context
import android.content.SharedPreferences
import com.ccooy.gankart.base.BaseApplication
import com.ccooy.gankart.manager.PrefsHelper
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

private const val PREFS_MODULE_TAG = "PrefsModule"

private const val PREFS_DEFAULT_SP_TAG = "PrefsDefault"

val prefsModule = Kodein.Module(PREFS_MODULE_TAG) {

    bind<SharedPreferences>(PREFS_DEFAULT_SP_TAG) with singleton {
        BaseApplication.INSTANCE.getSharedPreferences(PREFS_DEFAULT_SP_TAG, Context.MODE_PRIVATE)
    }

    bind<PrefsHelper>() with singleton {
        PrefsHelper(instance(PREFS_DEFAULT_SP_TAG))
    }
}