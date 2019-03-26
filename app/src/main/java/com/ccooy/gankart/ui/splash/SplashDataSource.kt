package com.ccooy.gankart.ui.splash

import com.ccooy.gankart.R
import com.ccooy.mvvm.base.repository.BaseRepositoryLocal
import com.ccooy.mvvm.base.repository.ILocalDataSource
import io.reactivex.Single
import java.util.*

interface ISplashLocalDataSource : ILocalDataSource {

    fun getPicUrl(): Single<Int>
}

val TRANSITION_URLS = arrayOf(R.drawable.ads1, R.drawable.ads2)

class SplashDataSourceRepository(
    localDataSource: ISplashLocalDataSource
) : BaseRepositoryLocal<ISplashLocalDataSource>(localDataSource) {

    fun getPicUrl(): Single<Int> =
        localDataSource.getPicUrl()
}

class SplashLocalDataSource : ISplashLocalDataSource {

    override fun getPicUrl(): Single<Int> =
        Single.just(TRANSITION_URLS[Random().nextInt(TRANSITION_URLS.size)])
}