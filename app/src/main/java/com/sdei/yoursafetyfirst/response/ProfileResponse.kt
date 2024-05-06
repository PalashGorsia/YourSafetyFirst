package com.app.yoursafetyfirst.response

data class ProfileResponse(
    val _id: String,
    val email: String,
    val gender: String,
    val phone: String,
    val roles: String,
    val name: String, val language: String,
    val date_registered: String,
    val isVerified: String,
    val yearOfBirth: String,
    val height: String,
    val weight: String,
    val restingHeartRate: String,
    val ringUse: Boolean,
    val ringId: String,
    val corporateCode: String
)


