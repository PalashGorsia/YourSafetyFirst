package com.app.yoursafetyfirst.response

data class RegisterResponse(
    val _id: String,
    val email: String,
    val gender: String,
    val loginToken: String,
    val phone: String,
    val roles: String,
    val name: String,
    val language: String,
    val ringUse: String,
    val resetPassword: Boolean
)




