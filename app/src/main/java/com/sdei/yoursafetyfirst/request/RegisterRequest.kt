package com.app.yoursafetyfirst.request

data class RegisterRequest(
    val name: String? = "",
    val email: String? = "",
    val phone: String? = "",
    val gender: String? = "",
    val password: String? = "",
    val confirm_password: String? = "",
    val status: String? = "",
    val deviceType: String? = "",
    val deviceToken: String? = "",
    val corporateCode: String? = "",
    val yearOfBirth: String? = "",
    val height: String? = "",
    val weight: String? = "",
    val restingHeartRate: String? = "",
    val ringId: String? = "",
    val ringUse: Boolean,
    val roles: String? = "",
    val language: String? = ""
)






