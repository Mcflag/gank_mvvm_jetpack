package com.ccooy.gankart.ui.login

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.ccooy.gankart.common.loadings.CommonLoadingViewModel
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton

const val LOGIN_MODULE_TAG = "LOGIN_MODULE_TAG"

val loginKodeinModule = Kodein.Module(LOGIN_MODULE_TAG) {
    bind<LoginViewModel>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        ViewModelProviders.of(context, LoginViewModelFactory.getInstance(instance())).get(LoginViewModel::class.java)
    }

    bind<CommonLoadingViewModel>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        CommonLoadingViewModel.instance()
    }

    bind<LoginRemoteDataSource>() with singleton {
        LoginRemoteDataSource(instance())
    }

    bind<LoginLocalDataSource>() with singleton {
        LoginLocalDataSource(instance(), instance())
    }

    bind<LoginDataSourceRepository>() with singleton {
        LoginDataSourceRepository(instance(), instance())
    }
}