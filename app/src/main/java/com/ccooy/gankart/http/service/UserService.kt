package com.ccooy.gankart.http.service

import com.ccooy.gankart.entity.ReceivedEvent
import com.ccooy.gankart.entity.Repo
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @GET("users/{username}/received_events?")
    fun queryReceivedEvents(
        @Path("username") username: String,
        @Query("page") pageIndex: Int,
        @Query("per_page") perPage: Int
    ): Flowable<List<ReceivedEvent>>

    @GET("users/{username}/repos?")
    fun queryRepos(
        @Path("username") username: String,
        @Query("page") pageIndex: Int,
        @Query("per_page") perPage: Int,
        @Query("sort") sort: String
    ): Flowable<List<Repo>>
}