package com.app.yoursafetyfirst.ui.language


import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.DriverSafetyApp
import com.app.yoursafetyfirst.databinding.ActivityLanguageBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.ui.MainActivity
import com.app.yoursafetyfirst.ui.login.LoginActivity
import com.app.yoursafetyfirst.utils.Language
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.LocaleHelper
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {

    private val viewModel: LanguageViewModel by viewModels()
    private var privacy: String = ""

    companion object {
        @JvmStatic
        fun start(context: Activity) {
            val starter = Intent(context, LanguageActivity::class.java)
            context.startActivity(starter)
            context.finishAffinity()
        }


    }

    override fun onCreate() {
        if (checkForInternet(this@LanguageActivity)) {
            lifecycleScope.launch {
                LocalData(this@LanguageActivity).token.first().let {
                    if (it.isNotEmpty()) {
                        viewModel.getLanguage(it)
                    }
                }
            }
        }

        lifecycleScope.launch {
            LocalData(this@LanguageActivity).privacyPolicy.first().let {
                privacy = it
            }
        }


        binding.japanese.setOnClickListener {
            navigateTo("ja")
        }

        binding.english.setOnClickListener {
            navigateTo("en")

        }
    }

    /* this method check if user is first time or not */
    private fun navigateTo(language: String) {
        LocaleHelper.setLocale(this@LanguageActivity, language)
        if (privacy.isNotEmpty()) {
            LoginActivity.start(this@LanguageActivity)
        } else {
            PrivacyPolicyActivity.start(this@LanguageActivity, language)
        }
    }

    override fun getViewBinding() = ActivityLanguageBinding.inflate(layoutInflater)

    override fun observer() {
        viewModel.languageResponse.observe(this) {
            when (it) {
                is NetworkResult.Error -> {
                    ShowSnackBar.showBarString(
                        binding.frameLayout,
                        it.message!!,
                        this@LanguageActivity,
                        ""
                    )
                }

                is NetworkResult.Loading -> {
                }

                is NetworkResult.Success -> {
                    if (it.data?.language == Language.English().x) {
                        LocaleHelper.setLocale(this@LanguageActivity, "en")
                        DriverSafetyApp.selectedLanguage = "en"
                        MainActivity.start(this@LanguageActivity)

                    } else {
                        LocaleHelper.setLocale(this@LanguageActivity, "ja")
                        DriverSafetyApp.selectedLanguage = "ja"
                        MainActivity.start(this@LanguageActivity)
                    }
                }

                is NetworkResult.Validation -> {
                    if (DriverSafetyApp.selectedLanguage == "Japanese" || DriverSafetyApp.selectedLanguage == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            this@LanguageActivity,
                            DriverSafetyApp.selectedLanguage
                        ) else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            this@LanguageActivity,
                            DriverSafetyApp.selectedLanguage
                        )

                }
            }
        }

    }
}