package com.ccooy.mvvm.binding.design

import androidx.databinding.BindingAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.MenuItem
import io.reactivex.functions.Consumer

interface SelectedChangeConsumer : Consumer<MenuItem>

@BindingAdapter("bind_onNavigationBottomSelectedChanged")
fun setOnSelectedChangeListener(
    view: BottomNavigationView,
    consumer: SelectedChangeConsumer?
) {
    view.setOnNavigationItemSelectedListener { item: MenuItem ->
        consumer?.accept(item)
        true
    }
}