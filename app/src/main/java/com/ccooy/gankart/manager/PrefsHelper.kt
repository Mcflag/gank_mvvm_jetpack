package com.ccooy.gankart.manager

import android.content.SharedPreferences
import com.ccooy.mvvm.util.prefs.boolean
import com.ccooy.mvvm.util.prefs.string

class PrefsHelper(prefs: SharedPreferences) {

    var autoLogin by prefs.boolean("autoLogin", true)

    var username by prefs.string("username", "")
    var password by prefs.string("password", "")
}