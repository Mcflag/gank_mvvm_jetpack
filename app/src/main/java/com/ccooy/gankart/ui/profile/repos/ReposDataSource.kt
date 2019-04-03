package com.ccooy.gankart.ui.profile.repos

import arrow.core.Either
import com.ccooy.gankart.entity.Errors
import com.ccooy.gankart.entity.Repo
import com.ccooy.gankart.http.globalHandleError
import com.ccooy.gankart.http.service.ServiceManager
import com.ccooy.mvvm.base.repository.BaseRepositoryBoth
import com.ccooy.mvvm.base.repository.ILocalDataSource
import com.ccooy.mvvm.base.repository.IRemoteDataSource
import com.ccooy.mvvm.util.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Flowable

interface IRemoteReposDataSource : IRemoteDataSource {
    fun queryRepos(
        username: String,
        pageIndex: Int,
        perPage: Int,
        sort: String
    ): Flowable<Either<Errors, List<Repo>>>
}

interface ILocalReposDataSource : ILocalDataSource {
    fun saveReposToLocal(repos: Either<Errors, List<Repo>>): Completable
}

class ReposDataSource(remote: IRemoteReposDataSource, local: ILocalReposDataSource) :
    BaseRepositoryBoth<IRemoteReposDataSource, ILocalReposDataSource>(remote, local) {
    fun queryRepos(username: String, pageIndex: Int, perPage: Int, sort: String): Flowable<Either<Errors, List<Repo>>> =
        remoteDataSource.queryRepos(username, pageIndex, perPage, sort)
            .flatMap { reposEither ->
                localDataSource.saveReposToLocal(reposEither).andThen(Flowable.just(reposEither))
            }
}

class RemoteReposDataSource(private val serviceManager: ServiceManager) : IRemoteReposDataSource {
    override fun queryRepos(
        username: String,
        pageIndex: Int,
        perPage: Int,
        sort: String
    ): Flowable<Either<Errors, List<Repo>>> {
        return serviceManager.userService.queryRepos(username, pageIndex, perPage, sort)
            .subscribeOn(RxSchedulers.io)
            .map {
                when (it.isEmpty()) {
                    true -> Either.left(Errors.EmptyResultsError)
                    false -> Either.right(it)
                }
            }
            .compose(globalHandleError())
    }
}

class LocalReposDataSource : ILocalReposDataSource {
    override fun saveReposToLocal(repos: Either<Errors, List<Repo>>): Completable {
        return Completable.complete()
    }
}
