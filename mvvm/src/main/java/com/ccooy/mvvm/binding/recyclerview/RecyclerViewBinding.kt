package com.ccooy.mvvm.binding.recyclerview

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.recyclerview.scrollStateChanges
import java.util.concurrent.TimeUnit

@BindingAdapter("bind_recyclerView_adapter")
fun bindAdapter(
    recyclerView: RecyclerView,
    adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?
) {
    adapter?.apply {
        recyclerView.adapter = adapter
    }
}

@SuppressLint("CheckResult")
@BindingAdapter(
    "bind_recyclerView_scrollStateChanges",
    "bind_recyclerView_scrollStateChanges_debounce",
    requireAll = false
)
fun setScrollStateChanges(
    recyclerView: RecyclerView,
    listener: ScrollStateChangesListener,
    debounce: Long = 500
) {
    recyclerView.scrollStateChanges()
        .debounce(debounce, TimeUnit.MILLISECONDS)
        .subscribe { state ->
            listener(state)
        }
}

typealias ScrollStateChangesListener = (Int) -> Unit