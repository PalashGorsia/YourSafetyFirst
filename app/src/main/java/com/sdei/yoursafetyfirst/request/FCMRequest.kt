package com.app.yoursafetyfirst.request

data class FCMRequest(
    val _id: String = "",
    val fcmToken: String = "",
    val deviceToken: String = ""
)

