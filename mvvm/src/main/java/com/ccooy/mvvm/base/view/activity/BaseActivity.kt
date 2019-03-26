package com.ccooy.mvvm.base.view.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.ccooy.mvvm.BR

abstract class BaseActivity<B : ViewDataBinding> : InjectionActivity() {

    protected lateinit var binding: B

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initView()
    }

    open fun initView() {

    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, layoutId)
        with(binding) {
            setVariable(BR.activity, this@BaseActivity)
            setLifecycleOwner(this@BaseActivity)
        }
    }
}