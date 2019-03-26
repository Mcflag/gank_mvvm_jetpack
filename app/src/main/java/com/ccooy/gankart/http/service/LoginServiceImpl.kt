package com.ccooy.gankart.http.service

import com.ccooy.gankart.di.BASE_URL
import com.ccooy.gankart.di.TIME_OUT_SECONDS
import com.ccooy.gankart.entity.LoginUser
import com.ccooy.gankart.http.interceptor.BasicAuthInterceptor
import io.reactivex.Flowable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class LoginServiceImpl(private val httpInterceptor: Interceptor) {

    fun login(
        username: String,
        password: String
    ): Flowable<LoginUser> {

        val client =
            OkHttpClient.Builder()
                .connectTimeout(
                    TIME_OUT_SECONDS.toLong(),
                    TimeUnit.SECONDS
                )
                .readTimeout(
                    TIME_OUT_SECONDS.toLong(),
                    TimeUnit.SECONDS
                )
                .addInterceptor(httpInterceptor)
                .addInterceptor(BasicAuthInterceptor(username, password))
                .build()

        val retrofit =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(LoginService::class.java).login()
    }
}