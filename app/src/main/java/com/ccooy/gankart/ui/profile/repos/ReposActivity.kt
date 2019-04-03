package com.ccooy.gankart.ui.profile.repos

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.core.content.ContextCompat.startActivity
import com.ccooy.gankart.R
import com.ccooy.gankart.base.BaseApplication
import com.ccooy.gankart.common.FabAnimateViewModel
import com.ccooy.gankart.databinding.ActivityReposBinding
import com.ccooy.gankart.databinding.ItemReposRepoBinding
import com.ccooy.gankart.entity.Repo
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

class ReposActivity : BaseActivity<ActivityReposBinding>() {
    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
        import(reposKodeinModule)
    }

    val viewModel: ReposViewModel by instance()

    val fabViewModel: FabAnimateViewModel by instance()

    override val layoutId: Int = R.layout.activity_repos

    val adapter = BasePagingDataBindingAdapter<Repo, ItemReposRepoBinding>(
        layoutId = R.layout.item_repos_repo,
        bindBinding = { ItemReposRepoBinding.bind(it) },
        callback = { repo, binding, _ ->
            binding.apply {
                data = repo
                repoEvent = object : Consumer2<String> {
                    override fun accept(t1: String, t2: String) {
                        WebViewActivity.launch(this@ReposActivity, t1, t2)
                    }
                }
                avatarEvent = object : Consumer2<String> {
                    override fun accept(t1: String, t2: String) {
                        WebViewActivity.launch(this@ReposActivity, t1, t2)
                    }
                }
            }
        }
    )

    override fun initView() {
        toolbar.inflateMenu(R.menu.menu_repos_filter_type)

        Completable
            .mergeArray(
                fabViewModel.visibleState.toReactiveStream()
                    .doOnNext { switchFabState(it) }
                    .ignoreElements(),
                viewModel.pagedList.toReactiveStream()
                    .doOnNext { adapter.submitList(it) }
                    .ignoreElements()
            )
            .autoDisposable(viewModel)
            .subscribe()
    }

    fun onMenuSelected(menuItem: MenuItem) {
        viewModel.sort.value = when (menuItem.itemId) {
            R.id.menu_repos_letter -> ReposViewModel.sortByLetter
            R.id.menu_repos_created -> ReposViewModel.sortByCreated
            R.id.menu_repos_update -> ReposViewModel.sortByUpdate
            else -> throw IllegalArgumentException("error menuItem id.")
        }
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
                startActivity(Intent(this, ReposActivity::class.java))
            }
    }
}