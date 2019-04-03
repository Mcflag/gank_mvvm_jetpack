package com.ccooy.gankart.ui.profile.events

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import arrow.core.Option
import arrow.core.none
import arrow.core.some
import com.ccooy.gankart.base.SimpleViewState
import com.ccooy.gankart.common.loadings.CommonLoadingState
import com.ccooy.gankart.entity.ReceivedEvent
import com.ccooy.gankart.manager.UserManager
import com.ccooy.mvvm.base.viewmodel.BaseViewModel
import com.ccooy.mvvm.ext.livedata.toReactiveStream
import com.ccooy.mvvm.ext.paging.IntPageKeyedData
import com.ccooy.mvvm.ext.paging.IntPageKeyedDataSource
import com.ccooy.mvvm.ext.paging.Paging
import com.ccooy.mvvm.util.SingletonHolderSingleArg
import com.uber.autodispose.autoDisposable
import io.reactivex.Flowable

class EventsViewModel(
    private val repo: EventsRepository
) : BaseViewModel() {
    private val events: MutableLiveData<List<ReceivedEvent>> = MutableLiveData()

    val pagedList = MutableLiveData<PagedList<ReceivedEvent>>()
    val refreshing: MutableLiveData<Boolean> = MutableLiveData()
    val loadingLayout: MutableLiveData<CommonLoadingState> = MutableLiveData()
    val error: MutableLiveData<Option<Throwable>> = MutableLiveData()

    init {
        refreshing.postValue(true)
        refreshing.toReactiveStream().filter { it }
            .doOnNext { initReceivedEvents() }
            .autoDisposable(this)
            .subscribe()
    }

    private fun initReceivedEvents() {
        Paging.buildReactiveStream(
            intPageKeyedDataSource = IntPageKeyedDataSource(
                loadInitial = {
                    queryReceivedEventsRefreshAction().flatMap { state ->
                        when (state) {
                            is SimpleViewState.Result -> Flowable.just(
                                IntPageKeyedData.build(
                                    data = state.result,
                                    pageIndex = 1,
                                    hasAdjacentPageKey = state.result.isNotEmpty()
                                )
                            )
                            else -> Flowable.empty()
                        }
                    }
                },
                loadAfter = {
                    queryReceivedEventsAction(it.key).flatMap { state ->
                        when (state) {
                            is SimpleViewState.Result -> Flowable.just(
                                IntPageKeyedData.build(
                                    data = state.result,
                                    pageIndex = it.requestedLoadSize,
                                    hasAdjacentPageKey = state.result.isNotEmpty()
                                )
                            )
                            else -> Flowable.empty()
                        }
                    }
                }
            )
        ).doOnNext { pagedList.postValue(it) }
            .autoDisposable(this)
            .subscribe()
    }

    private fun queryReceivedEventsAction(pageIndex: Int): Flowable<SimpleViewState<List<ReceivedEvent>>> =
        repo.queryReceivedEvents(UserManager.INSTANCE.login, pageIndex, 2)
            .map { either ->
                either.fold({
                    SimpleViewState.error<List<ReceivedEvent>>(it)
                }, {
                    SimpleViewState.result(it)
                })
            }.onErrorReturn { SimpleViewState.error(it) }

    private fun queryReceivedEventsRefreshAction(): Flowable<SimpleViewState<List<ReceivedEvent>>> =
        queryReceivedEventsAction(1)
            .startWith(SimpleViewState.loading())
            .startWith(SimpleViewState.idle())
            .doOnNext { state ->
                when (state) {
                    is SimpleViewState.Refreshing -> applyState()
                    is SimpleViewState.Idle -> applyState()
                    is SimpleViewState.Error -> applyState(
                        loadingLayout = CommonLoadingState.ERROR,
                        error = state.error.some()
                    )
                    is SimpleViewState.Result -> applyState(
                        events = state.result.some()
                    )
                }
            }
            .doFinally { refreshing.postValue(false) }

    private fun applyState(
        loadingLayout: CommonLoadingState = CommonLoadingState.IDLE,
        events: Option<List<ReceivedEvent>> = none(),
        error: Option<Throwable> = none()
    ) {
        this.loadingLayout.postValue(loadingLayout)
        this.error.postValue(error)
        this.events.postValue(events.orNull())
    }

    companion object {
        fun instance(activity: AppCompatActivity, repo: EventsRepository): EventsViewModel =
            ViewModelProviders.of(activity, EventsViewModelFactory.getInstance(repo))
                .get(EventsViewModel::class.java)
    }
}

class EventsViewModelFactory(
    private val repo: EventsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventsViewModel(repo) as T
    }

    companion object : SingletonHolderSingleArg<EventsViewModelFactory, EventsRepository>(::EventsViewModelFactory)
}