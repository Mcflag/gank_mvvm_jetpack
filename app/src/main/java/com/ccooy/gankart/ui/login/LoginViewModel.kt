package com.ccooy.gankart.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import arrow.core.*
import com.ccooy.gankart.base.SimpleViewState
import com.ccooy.gankart.common.loadings.CommonLoadingState
import com.ccooy.gankart.db.LoginEntity
import com.ccooy.gankart.entity.Errors
import com.ccooy.gankart.entity.LoginUser
import com.ccooy.gankart.http.globalHandleError
import com.ccooy.mvvm.base.viewmodel.BaseViewModel
import com.ccooy.mvvm.ext.arrow.whenNotNull
import com.ccooy.mvvm.ext.livedata.toReactiveStream
import com.ccooy.mvvm.util.SingletonHolderSingleArg
import com.uber.autodispose.autoDisposable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import retrofit2.HttpException

class LoginViewModel(
    private val repo: LoginDataSourceRepository
) : BaseViewModel() {
    val username: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()

    val loadingLayout: MutableLiveData<CommonLoadingState> = MutableLiveData()
    val error: MutableLiveData<Option<Throwable>> = MutableLiveData()
    val isShowMessage: MutableLiveData<Boolean> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()

    val userInfo: MutableLiveData<LoginUser> = MutableLiveData()
    private val autoLogin: MutableLiveData<Boolean> = MutableLiveData()

    init {
        autoLogin.toReactiveStream()
            .filter { it }
            .doOnNext { login() }
            .autoDisposable(this)
            .subscribe()

        error.toReactiveStream()
            .map { errorOption ->
                errorOption.flatMap {
                    when (it) {
                        is Errors.EmptyInputError -> "用户名或密码不能为空".some()
                        is HttpException ->
                            when (it.code()) {
                                401 -> "用户名或密码错误".some()
                                else -> "网络错误".some()
                            }
                        is Errors.EmptyResultsError -> "请输入用户名或密码".some()
                        else -> "未知错误".some()
                    }
                }
            }
            .autoDisposable(this)
            .subscribe {
                applyErrorState(true, it)
            }

        initAutoLogin().autoDisposable(this).subscribe()
    }

    private fun initAutoLogin() =
        Single
            .zip(
                repo.prefsUser().firstOrError(),
                repo.prefsAutoLogin(),
                BiFunction { either: Either<Errors, LoginEntity>, autoLogin: Boolean ->
                    autoLogin to either
                }
            )
            .doOnSuccess { pair ->
                pair.second.fold({ error ->
                    applyState(error = error.some())
                }, { entity ->
                    applyState(
                        username = entity.username.some(),
                        password = entity.password.some(),
                        autoLogin = pair.first
                    )
                })
            }

    fun login() {
        when (username.value.isNullOrEmpty() || password.value.isNullOrEmpty()) {
            true -> applyState(error = Errors.EmptyInputError.some())
            false -> repo
                .login(username.value!!, password.value!!)
                .compose(globalHandleError())
                .map { either ->
                    either.fold({
                        SimpleViewState.error<LoginUser>(it)
                    }, {
                        SimpleViewState.result(it)
                    })
                }
                .startWith(SimpleViewState.loading())
                .startWith(SimpleViewState.idle())
                .onErrorReturn { SimpleViewState.error(it) }
                .autoDisposable(this)
                .subscribe { state ->
                    when (state) {
                        is SimpleViewState.Refreshing -> applyState(loadingLayout = CommonLoadingState.LOADING)
                        is SimpleViewState.Idle -> applyState()
                        is SimpleViewState.Error -> applyState(
                            loadingLayout = CommonLoadingState.ERROR,
                            error = state.error.some()
                        )
                        is SimpleViewState.Result -> applyState(user = state.result.some())
                    }
                }
        }
    }

    private fun applyState(
        loadingLayout: CommonLoadingState = CommonLoadingState.IDLE,
        user: Option<LoginUser> = none(),
        error: Option<Throwable> = none(),
        username: Option<String> = none(),
        password: Option<String> = none(),
        autoLogin: Boolean = false
    ) {
        this.loadingLayout.postValue(loadingLayout)
        this.error.postValue(error)

        this.userInfo.postValue(user.orNull())

        username.whenNotNull { this.username.value = it }
        password.whenNotNull { this.password.value = it }

        this.autoLogin.postValue(autoLogin)
    }

    private fun applyErrorState(
        isShowMessage: Boolean = false,
        errorMessage: Option<String> = none()
    ) {
        this.isShowMessage.postValue(isShowMessage)
        errorMessage.whenNotNull { this.errorMessage.value = it }
    }
}

class LoginViewModelFactory(
    private val repo: LoginDataSourceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        LoginViewModel(repo) as T

    companion object :
        SingletonHolderSingleArg<LoginViewModelFactory, LoginDataSourceRepository>(::LoginViewModelFactory)
}