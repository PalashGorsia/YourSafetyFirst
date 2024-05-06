package com.app.yoursafetyfirst.response

data class Declaration(
    val __v: String,
    val _id: String,
    val bedTime: String,
    val createdAt: String,
    val do_meditation: String,
    val fatigue_existence: String,
    val isDeleted: Boolean,
    val meal: List<String>,
    val memo: String,
    val physical_condition: String,
    val sleepScore: String,
    val updatedAt: String,
    val userId: String,
    val wake_upTime: String
)