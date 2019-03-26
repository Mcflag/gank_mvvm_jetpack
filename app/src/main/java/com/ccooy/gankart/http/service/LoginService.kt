package com.ccooy.gankart.http.service

import com.ccooy.gankart.entity.LoginUser
import io.reactivex.Flowable
import retrofit2.http.GET

interface LoginService {

    @GET("user")
    fun login(): Flowable<LoginUser>
}