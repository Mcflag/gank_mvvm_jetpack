package com.ccooy.gankart.ui.profile

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import arrow.core.Option
import arrow.core.none
import arrow.core.toOption
import com.ccooy.gankart.entity.LoginUser
import com.ccooy.gankart.manager.UserManager
import com.ccooy.mvvm.base.viewmodel.BaseViewModel
import com.ccooy.mvvm.ext.arrow.whenNotNull
import com.ccooy.mvvm.util.SingletonHolderSingleArg

class ProfileViewModel(
    private val repo: ProfileRepository
) : BaseViewModel() {

    val error: MutableLiveData<Option<Throwable>> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val user: MutableLiveData<LoginUser> = MutableLiveData()

    init {
        applyState(user = UserManager.INSTANCE.toOption())
    }

    private fun applyState(
        isLoading: Boolean = false,
        user: Option<LoginUser> = none(),
        error: Option<Throwable> = none()
    ) {
        this.loading.postValue(isLoading)
        this.error.postValue(error)

        user.whenNotNull {
            this.user.postValue(it)
        }
    }

    companion object {
        fun instance(fragment: Fragment, repo: ProfileRepository): ProfileViewModel =
            ViewModelProviders
                .of(fragment, ProfileViewModelFactory.getInstance(repo))
                .get(ProfileViewModel::class.java)
    }
}

class ProfileViewModelFactory(
    private val repo: ProfileRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(repo) as T
    }

    companion object : SingletonHolderSingleArg<ProfileViewModelFactory, ProfileRepository>(::ProfileViewModelFactory)
}