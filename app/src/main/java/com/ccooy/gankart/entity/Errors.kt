package com.ccooy.gankart.entity

sealed class Errors : Throwable() {
    object NetworkError : Errors()
    object EmptyInputError : Errors()
    object EmptyResultsError : Errors()
    object SingleError : Errors()
}