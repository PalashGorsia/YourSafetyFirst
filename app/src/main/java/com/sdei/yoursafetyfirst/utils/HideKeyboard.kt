package com.app.yoursafetyfirst.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout

object HideKeyboard{

    fun hideKeyboard(activity: Activity, frameLayout: FrameLayout) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(frameLayout.windowToken, 0)
    }



}