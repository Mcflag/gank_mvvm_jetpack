package com.ccooy.gankart.utils

import com.ccooy.gankart.base.BaseApplication
import com.ccooy.mvvm.ext.toast

inline fun toast(value: () -> String): Unit =
    BaseApplication.INSTANCE.toast(value)