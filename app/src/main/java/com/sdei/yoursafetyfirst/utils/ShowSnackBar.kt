package com.app.yoursafetyfirst.utils

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar
import com.app.yoursafetyfirst.R
import java.util.Locale


object ShowSnackBar {

    fun showBar(view: View, msg: Int, activity: Activity, language: String) {

        val lan = when (language) {
            Language.English().x -> {
                "en"
            }

            Language.Japanese().x -> {
                "ja"
            }

            else -> {
                language
            }
        }

        val locale = Locale(lan)
        Locale.setDefault(locale)
        val configuration = activity.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        val languageMsg = activity.createConfigurationContext(configuration).getString(msg)

        val snackBar: Snackbar = Snackbar.make(view, languageMsg, Snackbar.LENGTH_LONG)
            .setTextMaxLines(3)
            .setTextColor(activity.resources.getColor(R.color.white))
            .setBackgroundTint(activity.resources.getColor(R.color.dark_blue))

        val snackBarLayout = snackBar.view
        val param: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
        param.gravity = Gravity.TOP
        param.width = FrameLayout.LayoutParams.MATCH_PARENT
        param.height = FrameLayout.LayoutParams.WRAP_CONTENT
        snackBarLayout.layoutParams = param
        snackBar.show()

    }


    fun showBarString(view: View, msg: String, activity: Activity, language: String) {

        val lan = when (language) {
            Language.English().x -> {
                "en"
            }

            Language.Japanese().x -> {
                "ja"
            }

            else -> {
                language
            }
        }

        val locale = Locale(lan)
        Locale.setDefault(locale)
        val configuration = activity.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        val snackBar: Snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
            .setTextMaxLines(3)
            .setTextColor(activity.resources.getColor(R.color.white))
            .setBackgroundTint(activity.resources.getColor(R.color.dark_blue))

        val snackBarLayout = snackBar.view
        val param: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
        param.gravity = Gravity.TOP
        param.width = FrameLayout.LayoutParams.MATCH_PARENT
        param.height = FrameLayout.LayoutParams.WRAP_CONTENT
        snackBarLayout.layoutParams = param
        snackBar.show()
    }
}