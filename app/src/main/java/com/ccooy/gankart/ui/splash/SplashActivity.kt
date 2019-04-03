package com.ccooy.gankart.ui.splash

import com.ccooy.gankart.R
import com.ccooy.gankart.databinding.ActivitySplashBinding
import com.ccooy.gankart.ui.login.LoginActivity
import com.ccooy.mvvm.base.view.activity.BaseActivity
import com.ccooy.mvvm.ext.livedata.toReactiveStream
import com.uber.autodispose.autoDisposable
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
        viewModel.complete
            .toReactiveStream()
            .doOnNext { toLogin() }
            .autoDisposable(scopeProvider)
            .subscribe()
    }

    fun toLogin() = LoginActivity.launch(this)

    companion object {
        private const val TAG = "SplashActivity"
    }
}