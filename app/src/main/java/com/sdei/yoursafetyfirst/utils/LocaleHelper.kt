package com.app.yoursafetyfirst.utils

import android.content.Context
import com.app.yoursafetyfirst.DriverSafetyApp
import com.app.yoursafetyfirst.YourSafetyFirstApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Locale


object LocaleHelper {

    // the method is used to set the language at runtime
    fun setLocale(context: Context, language: String): Context {
        YourSafetyFirstApp.selectedLanguage = language

        //setup language locally
        MainScope().launch {
            LocalData(context).storeSelectedLanguage(language)
        }

        // updating the language for devices above android nougat
        return updateResources(context, language)

    }


    // object of inbuilt Locale class and passing language argument to it
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }



}