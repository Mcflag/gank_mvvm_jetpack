package com.ccooy.gankart.ui.profile.events

import arrow.core.Either
import com.ccooy.gankart.entity.DISPLAY_EVENT_TYPES
import com.ccooy.gankart.entity.Errors
import com.ccooy.gankart.entity.ReceivedEvent
import com.ccooy.gankart.http.globalHandleError
import com.ccooy.gankart.http.service.ServiceManager
import com.ccooy.mvvm.base.repository.BaseRepositoryRemote
import com.ccooy.mvvm.base.repository.IRemoteDataSource
import com.ccooy.mvvm.util.RxSchedulers
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer

interface IRemoteEventsDataSource : IRemoteDataSource {
    fun queryReceivedEvents(
        username: String,
        pageIndex: Int,
        perPage: Int
    ): Flowable<Either<Errors, List<ReceivedEvent>>>

    fun filterEvents(): FlowableTransformer<List<ReceivedEvent>, List<ReceivedEvent>>
}

class EventsRepository(
    remoteDataSource: IRemoteEventsDataSource
) : BaseRepositoryRemote<IRemoteEventsDataSource>(remoteDataSource) {
    fun queryReceivedEvents(
        username: String,
        pageIndex: Int,
        perPage: Int
    ): Flowable<Either<Errors, List<ReceivedEvent>>> =
        remoteDataSource.queryReceivedEvents(username, pageIndex, perPage)
}

class EventsRemoteDataSource(private val serviceManager: ServiceManager) : IRemoteEventsDataSource {
    override fun queryReceivedEvents(
        username: String,
        pageIndex: Int,
        perPage: Int
    ): Flowable<Either<Errors, List<ReceivedEvent>>> =
        serviceManager.userService.queryReceivedEvents(username, pageIndex, perPage)
            .compose(filterEvents())
            .compose(globalHandleError())
            .subscribeOn(RxSchedulers.io)
            .map { list ->
                when (list.isEmpty()) {
                    true -> Either.left(Errors.EmptyResultsError)
                    false -> Either.right(list)
                }
            }


    override fun filterEvents(): FlowableTransformer<List<ReceivedEvent>, List<ReceivedEvent>> =
        FlowableTransformer { datas ->
            datas.flatMap {
                Flowable.fromIterable(it)
            }.filter { DISPLAY_EVENT_TYPES.contains(it.type) }
                .toList()
                .toFlowable()
        }
}
