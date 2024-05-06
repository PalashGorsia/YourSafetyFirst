package com.app.yoursafetyfirst.response


data class SinglePreviousData(
    val declaration: Declaration,
    val finalJudgement: List<FinalJudgement>,
    val observation: Observation,
    val reflexaction: Reflexaction,
    val score: Score
)