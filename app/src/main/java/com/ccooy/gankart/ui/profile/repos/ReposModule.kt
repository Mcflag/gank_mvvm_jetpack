package com.ccooy.gankart.ui.profile.repos

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ccooy.gankart.common.FabAnimateViewModel
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton

const val REPOS_MODULE_TAG = "REPOS_MODULE_TAG"

val reposKodeinModule = Kodein.Module(REPOS_MODULE_TAG) {

    bind<FabAnimateViewModel>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        FabAnimateViewModel()
    }

    bind<ReposViewModel>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        ReposViewModel.instance(
            activity = context,
            repo = instance()
        )
    }

    bind<ILocalReposDataSource>() with singleton {
        LocalReposDataSource()
    }

    bind<IRemoteReposDataSource>() with singleton {
        RemoteReposDataSource(instance())
    }

    bind<ReposDataSource>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        ReposDataSource(instance(), instance())
    }
}