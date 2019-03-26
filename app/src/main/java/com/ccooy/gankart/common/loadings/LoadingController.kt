package com.ccooy.gankart.common.loadings

import androidx.lifecycle.MutableLiveData

object LoadingController {

    @JvmStatic
    fun applyEmptyState(state: MutableLiveData<CommonLoadingState>): Boolean = state.value?.let { it ->
        when (it) {
            CommonLoadingState.EMPTY -> true
            CommonLoadingState.ERROR,
            CommonLoadingState.LOADING,
            CommonLoadingState.IDLE -> false
        }
    } ?: false

    @JvmStatic
    fun applyLoadingState(state: MutableLiveData<CommonLoadingState>): Boolean = state.value?.let { it ->
        when (it) {
            CommonLoadingState.LOADING -> true
            CommonLoadingState.ERROR,
            CommonLoadingState.EMPTY,
            CommonLoadingState.IDLE -> false
        }
    } ?: false

    @JvmStatic
    fun applyErrorState(state: MutableLiveData<CommonLoadingState>): Boolean = state.value?.let { it ->
        when (it) {
            CommonLoadingState.ERROR -> true
            CommonLoadingState.LOADING,
            CommonLoadingState.EMPTY,
            CommonLoadingState.IDLE -> false
        }
    } ?: false
}