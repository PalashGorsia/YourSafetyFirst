package com.app.yoursafetyfirst.repository

import com.app.yoursafetyfirst.response.BaseMessage

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val validationMessage: BaseMessage? = null,
) {

    class Success<T>(data: T) : NetworkResult<T>(data)

    class Error<T>(message: String?, data: T? = null) : NetworkResult<T>(data, message)

    class Loading<T>() : NetworkResult<T>()

    class Validation<T>(validationMessage: BaseMessage?): NetworkResult<T>(null,null,validationMessage)
}