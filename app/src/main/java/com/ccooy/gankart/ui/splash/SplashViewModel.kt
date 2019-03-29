@file:Suppress("UNCHECKED_CAST")

package com.ccooy.gankart.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.none
import arrow.core.some
import com.ccooy.mvvm.base.viewmodel.BaseViewModel
import com.ccooy.mvvm.util.SingletonHolderSingleArg
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


class SplashViewModel(
    private val repo: SplashDataSourceRepository
) : BaseViewModel() {

    val picUrl: MutableLiveData<Int> = MutableLiveData()
    val timer: MutableLiveData<Int> = MutableLiveData()
    val complete: MutableLiveData<Boolean> = MutableLiveData()

    init {
        getUrl()
        downCount()
    }

    private fun downCount() {
        Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
            .take(3)
            .map { 2 - it }
            .autoDisposable(this)
            .subscribe({
                applyState(time = it.toInt())
            }, {

            }, {
                applyState(isComplete = true.some())
            })
    }

    private fun getUrl() {
        repo.getPicUrl()
            .autoDisposable(this)
            .subscribe { id ->
                applyState(id)
            }
    }

    private fun applyState(
        drawableId: Int? = null,
        time: Int = 3,
        isComplete: Option<Boolean> = none()
    ) {
        drawableId?.let {
            this.picUrl.postValue(it)
        }
        this.complete.postValue(isComplete.orNull())
        if (isComplete.getOrElse { false }) {
            this.timer.postValue(0)
        } else {
            this.timer.postValue(time)
        }
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