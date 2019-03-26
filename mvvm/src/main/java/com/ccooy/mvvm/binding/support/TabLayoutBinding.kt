package com.ccooy.mvvm.binding.support

import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.ViewPager
import com.ccooy.mvvm.functional.Consumer
import com.google.android.material.tabs.TabLayout

@BindingAdapter(
    "bind_tabLayout_viewPager",
    requireAll = true
)
fun bindTabLayoutWithViewPager(
    tabLayout: TabLayout,
    viewPager: ViewPager
) = tabLayout.setupWithViewPager(viewPager)

@BindingAdapter(
    "bind_tabLayout_onTabSelected",
    "bind_tabLayout_onTabUnselected",
    "bind_tabLayout_onTabReselected",
    requireAll = false
)
fun bindTabLayoutSelectListener(
    tabLayout: TabLayout,
    onTabSelectedListener: OnTabLayoutTabSelectedListener?,
    onTabUnselectedListener: OnTabLayoutTabSelectedListener?,
    onTabReselectedListener: OnTabLayoutTabSelectedListener?
) {
    tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
            tab?.run {
                onTabReselectedListener?.accept(position)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            tab?.run {
                onTabUnselectedListener?.accept(position)
            }
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.run {
                onTabSelectedListener?.accept(position)
            }
        }
    })
}

interface OnTabLayoutTabSelectedListener : Consumer<Int>