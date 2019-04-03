package com.ccooy.gankart.ui.profile.repos

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import com.ccooy.gankart.base.SimpleViewState
import com.ccooy.gankart.entity.Repo
import com.ccooy.gankart.manager.UserManager
import com.ccooy.mvvm.base.viewmodel.BaseViewModel
import com.ccooy.mvvm.ext.livedata.toReactiveStream
import com.ccooy.mvvm.ext.paging.IntPageKeyedData
import com.ccooy.mvvm.ext.paging.IntPageKeyedDataSource
import com.ccooy.mvvm.ext.paging.Paging
import com.ccooy.mvvm.util.SingletonHolderSingleArg
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import io.reactivex.Flowable

@SuppressWarnings("checkResult")
class ReposViewModel(
    private val repo: ReposDataSource
) : BaseViewModel() {
    private val events: MutableLiveData<List<Repo>> = MutableLiveData()
    val sort: MutableLiveData<String> = MutableLiveData()
    val refreshing: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<Throwable> = MutableLiveData()
    val pagedList = MutableLiveData<PagedList<Repo>>()

    init {
        Completable.mergeArray(
            refreshing.toReactiveStream()
                .filter { it }
                .doOnNext { initReposList() }
                .ignoreElements(),
            sort.toReactiveStream()
                .distinctUntilChanged()
                .startWith(sortByLetter)
                .doOnNext { refreshing.postValue(true) }
                .ignoreElements()
        ).autoDisposable(this)
            .subscribe()
    }

    private fun initReposList() {
        Paging.buildReactiveStream(intPageKeyedDataSource = IntPageKeyedDataSource(
            loadInitial = {
                queryReposRefreshAction(it.requestedLoadSize)
                    .flatMap { state ->
                        when (state) {
                            is SimpleViewState.Result -> Flowable.just(
                                IntPageKeyedData.build(
                                    data = state.result,
                                    pageIndex = 1,
                                    hasAdjacentPageKey = state.result.size == it.requestedLoadSize
                                )
                            )
                            else -> Flowable.empty()
                        }
                    }
            },
            loadAfter = {
                queryReposAction(it.key, it.requestedLoadSize)
                    .flatMap { state ->
                        when (state) {
                            is SimpleViewState.Result -> Flowable.just(
                                IntPageKeyedData.build(
                                    data = state.result,
                                    pageIndex = it.key,
                                    hasAdjacentPageKey = state.result.size == it.requestedLoadSize
                                )
                            )
                            else -> Flowable.empty()
                        }
                    }
            }
        ))
            .doOnNext { pagedList.postValue(it) }
            .autoDisposable(this)
            .subscribe()
    }

    private fun queryReposAction(pageIndex: Int, pageSize: Int): Flowable<SimpleViewState<List<Repo>>> =
        repo
            .queryRepos(
                UserManager.INSTANCE.login,
                pageIndex, pageSize,
                sort.value ?: sortByLetter
            )
            .map { either ->
                either.fold({
                    SimpleViewState.error<List<Repo>>(it)
                }, {
                    SimpleViewState.result(it)
                })
            }
            .onErrorReturn { SimpleViewState.error(it) }

    private fun queryReposRefreshAction(pageSize: Int): Flowable<SimpleViewState<List<Repo>>> =
        queryReposAction(1, pageSize)
            .startWith(SimpleViewState.loading())
            .startWith(SimpleViewState.idle())
            .doOnNext { state ->
                when (state) {
                    is SimpleViewState.Refreshing -> applyState()
                    is SimpleViewState.Idle -> applyState()
                    is SimpleViewState.Error -> applyState(error = state.error)
                    is SimpleViewState.Result -> applyState(events = state.result)
                }
            }
            .doFinally { refreshing.postValue(false) }

    private fun applyState(
        events: List<Repo>? = null,
        error: Throwable? = null
    ) {
        this.error.postValue(error)
        this.events.postValue(events)
    }

    companion object {
        const val sortByCreated: String = "created"
        const val sortByUpdate: String = "updated"
        const val sortByLetter: String = "full_name"

        fun instance(
            activity: AppCompatActivity,
            repo: ReposDataSource
        ): ReposViewModel =
            ViewModelProviders.of(activity, ReposViewModelFactory.getInstance(repo))
                .get(ReposViewModel::class.java)
    }
}

@Suppress("UNCHECKED_CAST")
class ReposViewModelFactory(
    private val repo: ReposDataSource
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ReposViewModel(repo) as T
    }

    companion object : SingletonHolderSingleArg<ReposViewModelFactory, ReposDataSource>(::ReposViewModelFactory)
}