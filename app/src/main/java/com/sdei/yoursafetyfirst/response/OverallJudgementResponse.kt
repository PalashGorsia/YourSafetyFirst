package com.app.yoursafetyfirst.response

data class OverallJudgementResponse(
    val finalJudgement: List<FinalJudgement>
)

data class FinalJudgement(
    val __v: String,
    val _id: String,
    val colorCode: String,
    val createdAt: String,
    val description: Description,
    val isDeleted: Boolean,
    val level: Level,
    val max_value: String,
    val min_value: String,
    val title: Title,
    val updatedAt: String
)