package com.app.yoursafetyfirst.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhysicalResponse(
    val userId: String,
    val bedTime: String,
    val wake_upTime: String,
    val physical_condition: String,
    val meal: ArrayList<String>?,
    val fatigue_existence: String,
    val do_meditation: String,
    val memo: String = "-",
    val createdAt: String,
    val sleep_Time: String,
    val _id: String,
    val ringUse: Boolean
) : Parcelable {
    fun getMeals(): String? =
        meal?.let {
            meal.filterNotNull().filter { it!="null" && it.isNotEmpty() }.joinToString()
        } ?: kotlin.run { null }
}
