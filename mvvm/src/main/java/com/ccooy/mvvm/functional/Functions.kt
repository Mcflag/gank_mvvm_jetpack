package com.ccooy.mvvm.functional

typealias Supplier<T> = () -> T

interface Consumer<T> {

    fun accept(t: T)
}

interface Consumer2<T> {

    fun accept(t1: T, t2: T)
}