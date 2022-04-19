package com.example.util

sealed class Response<out R> {
    data class Success<T>(val data: T) : Response<T>()
    data class Error(val exception: Exception) : Response<Nothing>()

    fun takeIfSuccess(): R? = (this as? Success)?.data
    fun takeSuccess(): R = (this as Success).data
    fun takeIfError(): Exception? = (this as? Error)?.exception
    fun takeError(): Exception = (this as Error).exception
}