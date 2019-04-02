package com.ccooy.gankart.ui.main

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.ccooy.mvvm.base.viewmodel.BaseViewModel

class MainViewModel : BaseViewModel() {

    companion object {
        fun instance(activity: AppCompatActivity): MainViewModel =
            ViewModelProviders
                .of(activity, MainViewModelFactory)
                .get(MainViewModel::class.java)
    }
}

@Suppress("UNCHECKED_CAST")
object MainViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        MainViewModel() as T
}