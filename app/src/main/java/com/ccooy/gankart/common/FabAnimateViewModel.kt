package com.ccooy.gankart.common

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import arrow.core.left
import arrow.core.right
import com.ccooy.gankart.entity.Errors
import com.ccooy.mvvm.base.viewmodel.BaseViewModel
import com.ccooy.mvvm.util.RxSchedulers
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.subjects.PublishSubject

@SuppressLint("CheckResult")
class FabAnimateViewModel : BaseViewModel() {

    val visibleState: MutableLiveData<Boolean> = MutableLiveData()

    val stateChangesConsumer: (Int) -> Unit = {
        scrollStateSubject.onNext(it)
    }

    private val scrollStateSubject: PublishSubject<Int> = PublishSubject.create()

    init {
        scrollStateSubject
            .map { it == RecyclerView.SCROLL_STATE_IDLE }
            .compose { upstream ->
                upstream.zipWith(upstream.startWith(true)) { last, current ->
                    when (last == current) {
                        true -> Errors.EmptyInputError.left()
                        false -> current.right()
                    }
                }
            }
            .flatMap { changed ->
                changed.fold({
                    Observable.empty<Boolean>()
                }, {
                    Observable.just(it)
                })
            }
            .observeOn(RxSchedulers.ui)
            .autoDisposable(this)
            .subscribe {
                applyState(visible = it)
            }
    }

    private fun applyState(visible: Boolean) {
        visibleState.postValue(visible)
    }
}