package com.app.yoursafetyfirst.response

data class TrafficListResponse(
    val data: ArrayList<TrafficResponse>
)

data class TrafficResponse(
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



