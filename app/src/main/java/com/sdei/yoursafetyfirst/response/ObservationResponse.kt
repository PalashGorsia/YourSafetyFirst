package com.app.yoursafetyfirst.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class ObservationResponse(
    val observationData: ObservationResult,
    val QOLResult: QOLResult
)

data class ObservationResult(
    val QOL: String,
    val stressScore: String,
    val __v: String,
    val _id: String,
    val createdAt: String,
    val declarationId: String,
    val deviceType: String,
    val isDeleted: Boolean,
    val pulse: String,
    val updatedAt: String)

@Parcelize
data class QOLResult(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val description: Description,
    val images: List<String>,
    val isDeleted: Boolean,
    val level: Level,
    val max_value: Int,
    val min_value: Int,
    val led: Boolean,
    val vibrations: Boolean,
    val updatedAt: String,
    val colorCode: String,
    val vibrationTime: Double,
    val videos: List<Video>
) : Parcelable

@Parcelize
data class Level(
    val en: String,
    val ja: String
) : Parcelable

@Parcelize
data class Description(
    val en: String,
    val ja: String
) : Parcelable