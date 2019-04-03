package com.ccooy.gankart.ui.profile.events

import androidx.appcompat.app.AppCompatActivity
import com.ccooy.gankart.common.FabAnimateViewModel
import com.ccooy.gankart.common.loadings.CommonLoadingViewModel
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton

const val EVENTS_MODULE_TAG = "EVENTS_MODULE_TAG"

val eventsKodeinModule = Kodein.Module(EVENTS_MODULE_TAG) {
    bind<EventsViewModel>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        EventsViewModel.instance(
            activity = context,
            repo = instance()
        )
    }

    bind<FabAnimateViewModel>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        FabAnimateViewModel()
    }

    bind<CommonLoadingViewModel>() with scoped<AppCompatActivity>(AndroidLifecycleScope).singleton {
        CommonLoadingViewModel.instance()
    }

    bind<IRemoteEventsDataSource>() with singleton {
        EventsRemoteDataSource(instance())
    }

    bind<EventsRepository>() with singleton {
        EventsRepository(instance())
    }
}