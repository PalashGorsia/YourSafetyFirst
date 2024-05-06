package com.app.yoursafetyfirst.request

data class ChangePasswordRequest(
    val currentpwd: String? = "",
    val password: String? = "",
    val confirmPassword: String? = ""
)
