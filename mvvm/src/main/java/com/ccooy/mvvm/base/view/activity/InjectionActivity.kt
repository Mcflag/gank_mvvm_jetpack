package com.ccooy.mvvm.base.view.activity

import androidx.appcompat.app.AppCompatActivity
import com.ccooy.mvvm.base.view.IView
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.android.retainedKodein
import org.kodein.di.generic.kcontext

abstract class InjectionActivity : AutoDisposeActivity(), KodeinAware, IView {

    protected val parentKodein by closestKodein()

    override val kodeinContext = kcontext<AppCompatActivity>(this)

    override val kodein: Kodein by retainedKodein {
        extend(parentKodein, copy = Copy.All)
    }
}