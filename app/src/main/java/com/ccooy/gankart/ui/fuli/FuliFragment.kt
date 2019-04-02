package com.ccooy.gankart.ui.fuli

import com.ccooy.gankart.R
import com.ccooy.gankart.databinding.FragmentFuliBinding
import com.ccooy.gankart.utils.toast
import com.ccooy.mvvm.base.view.fragment.BaseFragment
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class FuliFragment : BaseFragment<FragmentFuliBinding>() {

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
//        import(profileKodeinModule)
    }

//    val viewModel: ProfileViewModel by instance()

    override val layoutId: Int = R.layout.fragment_fuli

    override fun initView() {

    }

    fun edit() = toast { "comming soon..." }
}