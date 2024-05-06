package com.app.yoursafetyfirst

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.ViewDataBinding
import com.app.yoursafetyfirst.utils.LocaleHelper


abstract class BaseActivity<B : ViewDataBinding> : AppCompatActivity() {

    lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)

        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE


        onCreate()
        observer()
    }

    abstract fun onCreate()

    abstract fun getViewBinding(): B

    abstract fun observer()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(
            LocaleHelper.setLocale(
                base,
                DriverSafetyApp.selectedLanguage
            )
        )
    }

}