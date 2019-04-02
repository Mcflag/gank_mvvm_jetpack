package com.ccooy.gankart.ui.xiandu

import com.ccooy.gankart.R
import com.ccooy.gankart.databinding.FragmentXianduBinding
import com.ccooy.gankart.utils.toast
import com.ccooy.mvvm.base.view.fragment.BaseFragment
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class XianduFragment : BaseFragment<FragmentXianduBinding>() {

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
//        import(profileKodeinModule)
    }

//    val viewModel: ProfileViewModel by instance()

    override val layoutId: Int = R.layout.fragment_xiandu

    override fun initView() {

    }

    fun edit() = toast { "comming soon..." }
}