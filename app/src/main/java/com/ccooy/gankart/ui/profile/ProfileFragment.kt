package com.ccooy.gankart.ui.profile

import com.ccooy.gankart.R
import com.ccooy.gankart.databinding.FragmentProfileBinding
import com.ccooy.gankart.ui.profile.events.EventsActivity
import com.ccooy.gankart.ui.profile.repos.ReposActivity
import com.ccooy.gankart.utils.toast
import com.ccooy.mvvm.base.view.fragment.BaseFragment
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
        import(profileKodeinModule)
    }

    val viewModel: ProfileViewModel by instance()

    override val layoutId: Int = R.layout.fragment_profile

    override fun initView() {

    }

    fun toRepos() = context?.let {
        ReposActivity.launch(it)
    }

    fun toEvents() = context?.let {
        EventsActivity.launch(it)
    }

    fun edit() = toast { "comming soon..." }

}