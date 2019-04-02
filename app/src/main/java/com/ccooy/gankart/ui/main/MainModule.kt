package com.ccooy.gankart.ui.main

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ccooy.gankart.ui.fuli.FuliFragment
import com.ccooy.gankart.ui.gank.GankFragment
import com.ccooy.gankart.ui.home.HomeFragment
import com.ccooy.gankart.ui.profile.ProfileFragment
import com.ccooy.gankart.ui.xiandu.XianduFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton

const val MAIN_MODULE_TAG = "MAIN_MODULE_TAG"

const val MAIN_LIST_FRAGMENT = "MAIN_LIST_FRAGMENT"

val mainKodeinModule = Kodein.Module(MAIN_MODULE_TAG) {

    bind<HomeFragment>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        HomeFragment()
    }

    bind<GankFragment>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        GankFragment()
    }

    bind<XianduFragment>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        XianduFragment()
    }

    bind<FuliFragment>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        FuliFragment()
    }

    bind<ProfileFragment>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        ProfileFragment()
    }

    bind<MainViewModel>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        MainViewModel.instance(context)
    }

    bind<BottomNavigationBar>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        (context as MainActivity).bottomNavigationBar
    }

    bind<ViewPager>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        (context as MainActivity).viewPager
    }

    bind<List<Fragment>>(MAIN_LIST_FRAGMENT) with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        listOf<Fragment>(
            instance<HomeFragment>(),
            instance<GankFragment>(),
            instance<XianduFragment>(),
            instance<FuliFragment>(),
            instance<ProfileFragment>()
        )
    }
}