package com.app.yoursafetyfirst.response

data class Score(
    val exerciseScore: String,
    val finalJudgementScore: String,
    val preFinalScore: String,
    val reflexScore: String,
    val selfDiagnosisScore: SelfDiagnosisScore,
    val sleepScore: String,
    val stressScore: String
)