package com.app.yoursafetyfirst.response

class ReflexResponse : ArrayList<ReflexResponseItem>()

data class ReflexResponseItem(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val isDeleted: Boolean,
    val reflexscreen1: String,
    val reflexscreen2: String,
    val updatedAt: String
)