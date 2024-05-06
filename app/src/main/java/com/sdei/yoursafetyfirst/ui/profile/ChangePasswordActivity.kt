package com.app.yoursafetyfirst.ui.profile

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.DriverSafetyApp
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.ActivityChangepasswordBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.ui.language.LanguageActivity
import com.app.yoursafetyfirst.ui.login.LoginActivity
import com.app.yoursafetyfirst.utils.HideKeyboard
import com.app.yoursafetyfirst.utils.Language
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordActivity : BaseActivity<ActivityChangepasswordBinding>() {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, ChangePasswordActivity::class.java)
            context.startActivity(starter)
        }
    }

    private val profileViewModel by viewModels<ProfileViewModel>()
    lateinit var language: String


    override fun onCreate() {
        binding.viewModel = profileViewModel

        binding.topBar.heading.text = getString(R.string.change_password)

        binding.topBar.imgBack.setOnClickListener {
            onBackPressed()
        }

        lifecycleScope.launch {
            LocalData(this@ChangePasswordActivity).language.first().let {
                language = if (it == "en") {
                    Language.English().x
                } else {
                    Language.Japanese().x

                }
            }
        }

        binding.changePassword.setOnClickListener {
            if (checkForInternet(this@ChangePasswordActivity)) {
                HideKeyboard.hideKeyboard(this, binding.frameLayout)
                lifecycleScope.launch {
                    LocalData(this@ChangePasswordActivity).token.first().let {
                        if (it.isNotEmpty()) {
                            profileViewModel.changePassword(it)
                        }
                    }
                }

            } else {
                ShowSnackBar.showBar(
                    binding.frameLayout,
                    R.string.no_internt,
                    this@ChangePasswordActivity,
                    DriverSafetyApp.selectedLanguage

                )
            }
        }
    }

    override fun getViewBinding(): ActivityChangepasswordBinding =
        ActivityChangepasswordBinding.inflate(layoutInflater)

    override fun observer() {
        profileViewModel.changePasswordResponse.observe(this) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    if (it.message == "401") {
                        lifecycleScope.launch {
                            LocalData(this@ChangePasswordActivity).clearDataStore()
                            LanguageActivity.start(this@ChangePasswordActivity)
                        }
                    } else {
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.message!!,
                            this@ChangePasswordActivity,
                            language
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true
                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false
                    lifecycleScope.launch {
                        LocalData(this@ChangePasswordActivity).clearDataStore()
                        LocalData(this@ChangePasswordActivity).clearSaveCredentials()
                        LoginActivity.startWithFinish(this@ChangePasswordActivity)
                    }
                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (DriverSafetyApp.selectedLanguage == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            this@ChangePasswordActivity,
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            this@ChangePasswordActivity,
                            language
                        )
                }
            }
        }

        profileViewModel.validationResponse.observe(this) {
            ShowSnackBar.showBar(binding.frameLayout, it, this, language)
        }
    }
}