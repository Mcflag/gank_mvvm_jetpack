package com.ccooy.gankart.http

import com.ccooy.gankart.utils.toast
import com.github.qingmei2.core.GlobalErrorTransformer
import retrofit2.HttpException

fun <T> globalHandleError(): GlobalErrorTransformer<T> = GlobalErrorTransformer(
    globalDoOnErrorConsumer = { error ->
        when (error) {
            is HttpException -> {
                when (error.code()) {
                    401 -> toast { "401 Unauthorized" }
                    404 -> toast { "404 error" }
                    500 -> toast { "500 server error" }
                    else -> toast { "network error" }
                }
            }
            else -> toast { "network error" }
        }
    }
)