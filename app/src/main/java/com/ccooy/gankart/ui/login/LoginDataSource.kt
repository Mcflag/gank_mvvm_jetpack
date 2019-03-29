package com.ccooy.gankart.ui.login

import arrow.core.Either
import com.ccooy.gankart.db.LoginEntity
import com.ccooy.gankart.db.UserDatabase
import com.ccooy.gankart.entity.Errors
import com.ccooy.gankart.entity.LoginUser
import com.ccooy.gankart.http.service.ServiceManager
import com.ccooy.gankart.manager.PrefsHelper
import com.ccooy.gankart.manager.UserManager
import com.ccooy.mvvm.base.repository.BaseRepositoryBoth
import com.ccooy.mvvm.base.repository.ILocalDataSource
import com.ccooy.mvvm.base.repository.IRemoteDataSource
import com.ccooy.mvvm.util.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface ILoginRemoteDataSource : IRemoteDataSource {
    fun login(username: String, password: String): Flowable<Either<Errors, LoginUser>>
}

interface ILoginLocalDataSource : ILocalDataSource {
    fun savePrefsUser(username: String, password: String): Completable

    fun fetchPrefsUser(): Flowable<Either<Errors, LoginEntity>>

    fun isAutoLogin(): Single<Boolean>
}

class LoginDataSourceRepository(
    remoteDataSource: ILoginRemoteDataSource,
    localDataSource: ILoginLocalDataSource
) : BaseRepositoryBoth<ILoginRemoteDataSource, ILoginLocalDataSource>(remoteDataSource, localDataSource) {
    fun login(username: String, password: String): Flowable<Either<Errors, LoginUser>> =
        remoteDataSource.login(username, password)
            .doOnNext { either ->
                either.fold({

                }, {
                    UserManager.INSTANCE = it
                })
            }
            .flatMap {
                localDataSource.savePrefsUser(username, password)
                    .andThen(Flowable.just(it))
            }

    fun prefsUser(): Flowable<Either<Errors, LoginEntity>> =
        localDataSource.fetchPrefsUser()

    fun prefsAutoLogin(): Single<Boolean> =
        localDataSource.isAutoLogin()
}

class LoginRemoteDataSource(
    private val serviceManager: ServiceManager
) : ILoginRemoteDataSource {
    override fun login(username: String, password: String): Flowable<Either<Errors, LoginUser>> =
        serviceManager.loginService
            .login(username, password)
            .subscribeOn(RxSchedulers.io)
            .map {
                Either.right(it)
            }
}

class LoginLocalDataSource(
    private val database: UserDatabase,
    private val prefs: PrefsHelper
) : ILoginLocalDataSource {
    override fun isAutoLogin(): Single<Boolean> =
        Single.just(prefs.autoLogin)

    override fun savePrefsUser(username: String, password: String): Completable =
        Completable.fromAction {
            prefs.username = username
            prefs.password = password
        }

    override fun fetchPrefsUser(): Flowable<Either<Errors, LoginEntity>> =
        Flowable.just(prefs)
            .map {
                when (it.username.isNotEmpty() && it.password.isNotEmpty()) {
                    true -> Either.right(LoginEntity(1, it.username, it.password))
                    false -> Either.left(Errors.EmptyResultsError)
                }
            }

}