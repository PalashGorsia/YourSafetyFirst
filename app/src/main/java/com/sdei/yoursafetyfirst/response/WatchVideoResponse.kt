package com.app.yoursafetyfirst.response


data class WatchVideoResponse(
    val pulse: String,
    val QOL: String,
    val steps: String,
    val exerciseScore: String,
    val stressScore: String,
    val sleepScore: String,
    val watchedVideo: Boolean,
    val _id: String,
    val declarationId: String
)
