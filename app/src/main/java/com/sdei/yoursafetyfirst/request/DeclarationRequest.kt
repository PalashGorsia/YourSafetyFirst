package com.app.yoursafetyfirst.request

data class DeclarationRequest(
    val userId: String?="",
    val bedTime: String?="",
    val wake_upTime: String?="",
    val meal: ArrayList<String>,
    val physical_condition: String?="",
    val fatigue_existence: String?="",
    val do_meditation: String?="",
    val memo: String?="",
    val sleep_Time: String?="")
