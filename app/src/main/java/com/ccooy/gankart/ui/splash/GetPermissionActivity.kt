package com.ccooy.gankart.ui.splash

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.ccooy.mvvm.base.view.activity.PermissionActivity

class GetPermissionActivity : PermissionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LinearLayout(this))
        requestStorage()
    }

    override fun writeStorage() {
        startActivity(Intent(this, SplashActivity::class.java))
    }
}