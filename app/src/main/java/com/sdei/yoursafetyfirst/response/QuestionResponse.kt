package com.app.yoursafetyfirst.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionResponse(
    val questionnaire: List<Questionnaire>,
    val total: Int
) : Parcelable

@Parcelize
data class Questionnaire(
    val __v: String,
    val _id: String,
    val ansType: String,
    val createdAt: String,
    val isDeleted: Boolean,
    val options: List<Option>,
    val questionName: QuestionName,
    val updatedAt: String
) : Parcelable

@Parcelize
data class Option(
    val _id: String,
    val name: Name,
    val scoreVal: String,
    var selected: Boolean = false
) : Parcelable

@Parcelize
data class QuestionName(
    val en: String,
    val ja: String
) : Parcelable

@Parcelize
data class Name(
    val en: String,
    val ja: String
) : Parcelable