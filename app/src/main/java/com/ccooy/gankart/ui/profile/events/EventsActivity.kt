package com.ccooy.gankart.ui.profile.events

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import com.ccooy.gankart.R
import com.ccooy.gankart.base.BaseApplication
import com.ccooy.gankart.common.FabAnimateViewModel
import com.ccooy.gankart.common.loadings.CommonLoadingState
import com.ccooy.gankart.common.loadings.CommonLoadingViewModel
import com.ccooy.gankart.databinding.ActivityEventsBinding
import com.ccooy.gankart.databinding.ItemEventsReceivedEventBinding
import com.ccooy.gankart.entity.ReceivedEvent
import com.ccooy.gankart.ui.main.WebViewActivity
import com.ccooy.mvvm.adapter.BasePagingDataBindingAdapter
import com.ccooy.mvvm.base.view.activity.BaseActivity
import com.ccooy.mvvm.ext.jumpBrowser
import com.ccooy.mvvm.ext.livedata.toReactiveStream
import com.ccooy.mvvm.functional.Consumer
import com.ccooy.mvvm.functional.Consumer2
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import kotlinx.android.synthetic.main.activity_repos.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class EventsActivity : BaseActivity<ActivityEventsBinding>() {
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
        import(eventsKodeinModule)
    }

    val viewModel: EventsViewModel by instance()
    val fabViewModel: FabAnimateViewModel by instance()
    val loadingViewModel: CommonLoadingViewModel by instance()

    override val layoutId: Int = R.layout.activity_events

    val adapter: BasePagingDataBindingAdapter<ReceivedEvent, ItemEventsReceivedEventBinding> =
        BasePagingDataBindingAdapter(
            layoutId = R.layout.item_events_received_event,
            bindBinding = {
                ItemEventsReceivedEventBinding.bind(it)
            },
            callback = { data, binding, _ ->
                binding.data = data
                binding.actorEvent = object : Consumer2<String> {
                    override fun accept(t1: String, t2: String) {
                        WebViewActivity.launch(this@EventsActivity, t1, t2)
                    }
                }
                binding.repoEvent = object : Consumer2<String> {
                    override fun accept(t1: String, t2: String) {
                        WebViewActivity.launch(this@EventsActivity, t1, t2)
                    }
                }
            }
        )

    override fun initView() {
        Completable
            .mergeArray(
                fabViewModel.visibleState
                    .toReactiveStream()
                    .doOnNext { switchFabState(it) }
                    .ignoreElements(),
                viewModel.loadingLayout
                    .toReactiveStream()
                    .filter { it ->
                        it != CommonLoadingState.LOADING    // Refreshing state not used
                    }
                    .doOnNext { loadingViewModel.applyState(it) }
                    .ignoreElements(),
                viewModel.pagedList
                    .toReactiveStream()
                    .doOnNext { adapter.submitList(it) }
                    .ignoreElements()
            )
            .autoDisposable(scopeProvider)
            .subscribe()
    }

    private fun switchFabState(show: Boolean) =
        when (show) {
            false -> ObjectAnimator.ofFloat(fabTop, "translationX", 250f, 0f)
            true -> ObjectAnimator.ofFloat(fabTop, "translationX", 0f, 250f)
        }.apply {
            duration = 100
            start()
        }

    companion object {
        fun launch(mContext: Context) =
            mContext.apply {
                startActivity(Intent(this, EventsActivity::class.java))
            }
    }
}