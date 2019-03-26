@file:Suppress("UNCHECKED_CAST")

package com.ccooy.gankart.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ccooy.gankart.R
import com.ccooy.mvvm.base.viewmodel.BaseViewModel
import com.ccooy.mvvm.util.SingletonHolderSingleArg
import com.uber.autodispose.autoDisposable

class SplashViewModel(
    private val repo: SplashDataSourceRepository
) : BaseViewModel() {

    val picUrl: MutableLiveData<Int> = MutableLiveData()

    init {
        applyState()
    }

    fun getUrl() {
        repo.getPicUrl()
            .autoDisposable(this)
            .subscribe { id ->
                applyState(id)
            }
    }

    private fun applyState(
        drawableId: Int = R.drawable.img_transition_default
    ) {
        this.picUrl.postValue(drawableId)
    }
}

class SplashViewModelFactory(
    private val repo: SplashDataSourceRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        SplashViewModel(repo) as T

    companion object :
        SingletonHolderSingleArg<SplashViewModelFactory, SplashDataSourceRepository>(::SplashViewModelFactory)
}