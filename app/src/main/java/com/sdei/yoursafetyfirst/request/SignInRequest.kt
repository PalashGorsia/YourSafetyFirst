package com.app.yoursafetyfirst.request

data class SignInRequest(
    val email: String? = "",
    val password: String? = "",
    val deviceToken: String? = "",
    val fcmToken: String? = "",
    val loginType: String? = "DRIVER",
    val language: String? = "",
    val deviceType: String? = ""
)

