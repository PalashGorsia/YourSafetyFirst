package com.app.yoursafetyfirst.response

data class Observation(
    val QOL: String,
    val __v: String,
    val _id: String,
    val createdAt: String,
    val declarationId: String,
    val deviceType: String,
    val exerciseScore: Float,
    val isDeleted: Boolean,
    val pulse: String,
    val pulseVariation: List<Any>,
    val sleepScore: String,
    val steps: String,
    val stressScore: String,
    val updatedAt: String
)