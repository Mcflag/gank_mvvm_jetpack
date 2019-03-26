package com.ccooy.gankart.ui.splash

import com.ccooy.gankart.R
import com.ccooy.gankart.databinding.ActivitySplashBinding
import com.ccooy.mvvm.base.view.activity.BaseActivity
import com.ccooy.mvvm.ext.toast
import com.ccooy.mvvm.logger.log
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
        import(splashKodeinModule)
    }

    override val layoutId: Int = R.layout.activity_splash

    val viewModel: SplashViewModel by instance()

    override fun initView() {
        viewModel.getUrl()
    }

    fun getDrawable() {
        viewModel.getUrl()
        log { "" + viewModel.picUrl.value }
        toast("" + viewModel.picUrl.value)
    }

    companion object {
        private const val TAG = "SplashFragment"
    }
}