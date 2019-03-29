package com.ccooy.gankart.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.ccooy.gankart.R
import com.ccooy.gankart.common.loadings.CommonLoadingViewModel
import com.ccooy.gankart.databinding.ActivityLoginBinding
import com.ccooy.gankart.ui.main.MainActivity
import com.ccooy.mvvm.base.view.activity.BaseActivity
import com.ccooy.mvvm.ext.livedata.toReactiveStream
import com.uber.autodispose.autoDisposable
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
        import(loginKodeinModule)
    }

    override val layoutId: Int = R.layout.activity_login

    val viewModel: LoginViewModel by instance()

    val loadingViewModel: CommonLoadingViewModel by instance()

    override fun initView() {
        viewModel.userInfo
            .toReactiveStream()
            .doOnNext { toMain() }
            .autoDisposable(scopeProvider)
            .subscribe()

        viewModel.loadingLayout
            .toReactiveStream()
            .doOnNext { loadingViewModel.applyState(it) }
            .autoDisposable(scopeProvider)
            .subscribe()
    }

    fun login() = viewModel.login()

    fun toMain() = MainActivity.launch(this)

    companion object {
        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
    }
}