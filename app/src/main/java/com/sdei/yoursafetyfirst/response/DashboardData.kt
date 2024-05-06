package com.app.yoursafetyfirst.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DashboardData(
    val url: String,
    val title: Title,
    val description: Description
) : Parcelable
