package com.ccooy.gankart.ui.main

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.ccooy.mvvm.base.view.activity.BaseActivity
import com.ccooy.gankart.R
import com.ccooy.gankart.databinding.ActivityMainBinding
import com.ccooy.mvvm.ext.toast
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance

class MainActivity : BaseActivity<ActivityMainBinding>(), BottomNavigationBar.OnTabSelectedListener {

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
        import(mainKodeinModule)
        bind<FragmentManager>() with instance(supportFragmentManager)
    }

    override val layoutId = R.layout.activity_main

    // 保存用户按返回键的时间
    private var mExitTime: Long = 0
    
    val viewModel: MainViewModel by instance()
    val fragments: List<Fragment> by instance(MAIN_LIST_FRAGMENT)

    override fun initView() {
        requestStorage()
        bottomNavigationBar.setTabSelectedListener(this)
        bottomNavigationBar
            .setMode(BottomNavigationBar.MODE_FIXED) // 设置mode
            .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)  // 背景样式
            .setBarBackgroundColor(R.color.bg_grey) // 背景颜色
            .setInActiveColor(R.color.grey88) // 未选中状态颜色
            .setActiveColor(R.color.dark_grey) // 选中状态颜色
            .addItem(
                BottomNavigationItem(
                    R.drawable.home_filled,
                    "首页"
                ).setInactiveIconResource(R.drawable.home)
            ) // 添加Item
            .addItem(
                BottomNavigationItem(
                    R.drawable.hot_article_filled,
                    "分类"
                ).setInactiveIconResource(R.drawable.hot_article)
            )
            .addItem(
                BottomNavigationItem(
                    R.drawable.magazine_filled,
                    "闲读"
                ).setInactiveIconResource(R.drawable.magazine)
            )
            .addItem(
                BottomNavigationItem(
                    R.drawable.gift_filled,
                    "福利"
                ).setInactiveIconResource(R.drawable.gift)
            )
            .addItem(
                BottomNavigationItem(
                    R.drawable.user_filled,
                    "我的"
                ).setInactiveIconResource(R.drawable.user)
            )
            .initialise()  // 提交初始化（完成配置）
        bottomNavigationBar.setFirstSelectedPosition(0)
    }

    override fun onTabReselected(position: Int) {
    }

    override fun onTabUnselected(position: Int) {
    }

    override fun onTabSelected(position: Int) {
        viewPager.currentItem = position
    }

    fun onPageSelectChangedPort(index: Int) {
        if (bottomNavigationBar.visibility == View.VISIBLE)
            bottomNavigationBar.selectTab(index)
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            toast { resources.getString(R.string.exit_toast) }
            mExitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }
}