package com.ccooy.gankart.ui.profile

import com.ccooy.gankart.http.service.ServiceManager
import com.ccooy.mvvm.base.repository.BaseRepositoryRemote
import com.ccooy.mvvm.base.repository.IRemoteDataSource

interface IRemoteProfileDataSource : IRemoteDataSource

class ProfileRepository(
    remoteDataSource: IRemoteProfileDataSource
) : BaseRepositoryRemote<IRemoteProfileDataSource>(remoteDataSource)

class ProfileRemoteDataSource(val serviceManager: ServiceManager) : IRemoteProfileDataSource