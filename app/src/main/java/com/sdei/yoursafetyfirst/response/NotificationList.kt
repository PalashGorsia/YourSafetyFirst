package com.app.yoursafetyfirst.response

data class NotificationList(
    val notifications: List<Notification>,
    val total: Int
)