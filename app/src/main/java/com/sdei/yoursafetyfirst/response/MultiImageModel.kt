package com.app.yoursafetyfirst.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MultiImageModel(
    val type: String,
    val videoUrl: String,
    val imageUrl: String
) : Parcelable
