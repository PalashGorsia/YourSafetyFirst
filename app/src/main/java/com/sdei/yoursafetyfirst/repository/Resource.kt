package com.app.yoursafetyfirst.repository

import com.app.yoursafetyfirst.response.BaseMessage

data class Resource<out T>(
    val statusCode: Int?,
    val data: T?,
    val message: BaseMessage?
)