package com.ccooy.gankart.ui.main

import android.content.Intent
import com.ccooy.mvvm.base.view.activity.BaseActivity
import com.ccooy.gankart.R
import com.ccooy.gankart.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutId = R.layout.activity_main

    companion object {

        fun launch(activity: androidx.fragment.app.FragmentActivity) =
            activity.apply {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }
}