package com.app.yoursafetyfirst.response

data class SelfDiagnosisScore(
    val meal: String,
    val medication: String,
    val pyhsicalCondition: String,
    val tired: String,
    val totalDeduction: String
)