package com.app.yoursafetyfirst.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class DashboardResponse(
    val data: List<Data>,
    val total: Int
)

data class Data(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val description: Description,
    val frequency: String,
    val images: List<String>,
    val isDeleted: Boolean,
    val title: Title,
    val type: String,
    val updatedAt: String,
    val url: String,
    val videos: List<Video>
)


@Parcelize
data class Video(
    val __v: Int,
    val videoURL: String,
    val thumbnailURL: String?,
):Parcelable