package com.app.yoursafetyfirst.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    val _id: String?=null,
    val createdAt: String?=null,
    val message: Message?=null,
    val title: Title?=null,
    val type: String?=null,
    val image: ArrayList<String>
) : Parcelable


@Parcelize
data class Message(
    val en: String?=null,
    val ja: String?=null
) : Parcelable

@Parcelize
data class Title(
    val en: String?=null,
    val ja: String?=null
) : Parcelable