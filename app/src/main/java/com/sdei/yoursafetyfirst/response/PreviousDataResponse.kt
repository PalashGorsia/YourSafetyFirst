package com.app.yoursafetyfirst.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PreviousDataResponse(
    val data: List<DataX>,
    val total: Int
) : Parcelable


@Parcelize
data class DataX(
    val _id: String,
    val createdAt: String,
    val declarationId: String,
    val description: Description,
    val level: Level,
    val title: Title,
    val updatedAt: String,
    val userId: String,
    val colorCode: String? = null
) : Parcelable