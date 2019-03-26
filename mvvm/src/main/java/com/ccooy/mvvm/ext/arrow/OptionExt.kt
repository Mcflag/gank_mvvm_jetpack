package com.ccooy.mvvm.ext.arrow

import arrow.core.Option

inline fun <T> Option<T>.whenNotNull(consumer: (T) -> Unit) =
    fold({}, consumer)

inline fun <T> Option<T>.whenEmpty(action: () -> Unit) =
    fold(action, {})