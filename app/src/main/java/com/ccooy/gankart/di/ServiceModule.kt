package com.ccooy.gankart.di

import com.ccooy.gankart.http.service.LoginServiceImpl
import com.ccooy.gankart.http.service.ServiceManager
import com.ccooy.gankart.http.service.UserService
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit

private const val SERVICE_MODULE_TAG = "serviceModule"

val serviceModule = Kodein.Module(SERVICE_MODULE_TAG) {

    bind<LoginServiceImpl>() with singleton {
        LoginServiceImpl(instance(HTTP_CLIENT_MODULE_INTERCEPTOR_LOG_TAG))
    }

    bind<ServiceManager>() with singleton {
        ServiceManager(instance(), instance())
    }

    bind<UserService>() with singleton {
        instance<Retrofit>().create(UserService::class.java)
    }
}