package com.app.yoursafetyfirst.ui.forgotpassword

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.DriverSafetyApp
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.ActivityForgotPasswordBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.ui.language.LanguageActivity
import com.app.yoursafetyfirst.ui.login.LoginActivity
import com.app.yoursafetyfirst.utils.GenericTextWatcher
import com.app.yoursafetyfirst.utils.HideKeyboard
import com.app.yoursafetyfirst.utils.Language
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding>() {

    companion object {
        @JvmStatic
        fun start(context: Activity) {
            val starter = Intent(context, ForgotPasswordActivity::class.java)
            context.startActivity(starter)
        }
    }

    var id: String = ""
    private val viewModel: ForgotViewModel by viewModels()
    var language: String = ""
    var i = 0
    var job: Job? = null

    override fun onCreate() {
        binding.viewModel = viewModel
        viewModel.setScreenNumber(1)



        lifecycleScope.launch {
            LocalData(this@ForgotPasswordActivity).language.first().let {
                language = if (it == "en") {
                    Language.English().x
                } else {
                    Language.Japanese().x
                }
            }
        }

        binding.btnForgotPassword.setOnClickListener {
            if (checkForInternet(this@ForgotPasswordActivity)) {
                lifecycleScope.launch {
                    HideKeyboard.hideKeyboard(this@ForgotPasswordActivity, binding.frameLayout)
                    viewModel.sendEmail()
                }
            } else {
                ShowSnackBar.showBar(
                    binding.frameLayout,
                    R.string.no_internt,
                    this@ForgotPasswordActivity,
                    DriverSafetyApp.selectedLanguage

                )
            }

        }

        binding.btnOtp.setOnClickListener {
            if (checkForInternet(this@ForgotPasswordActivity)) {
                lifecycleScope.launch {
                    HideKeyboard.hideKeyboard(this@ForgotPasswordActivity, binding.frameLayout)
                    viewModel.verifyOTP(id)
                }
            } else {
                ShowSnackBar.showBar(
                    binding.frameLayout,
                    R.string.no_internt,
                    this@ForgotPasswordActivity,
                    DriverSafetyApp.selectedLanguage

                )
            }

        }

        binding.btnResetPassword.setOnClickListener {
            if (checkForInternet(this@ForgotPasswordActivity)) {
                lifecycleScope.launch {
                    HideKeyboard.hideKeyboard(this@ForgotPasswordActivity, binding.frameLayout)
                    viewModel.resetPassword(id)
                }
            } else {
                ShowSnackBar.showBar(
                    binding.frameLayout,
                    R.string.no_internt,
                    this@ForgotPasswordActivity,
                    DriverSafetyApp.selectedLanguage

                )
            }
        }

        binding.topBar.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.input1.addTextChangedListener(GenericTextWatcher(binding.input2, binding.input1))
        binding.input2.addTextChangedListener(GenericTextWatcher(binding.input3, binding.input1))
        binding.input3.addTextChangedListener(GenericTextWatcher(binding.input4, binding.input2))
        binding.input4.addTextChangedListener(GenericTextWatcher(binding.input5, binding.input3))
        binding.input5.addTextChangedListener(GenericTextWatcher(binding.input5, binding.input4))

        binding.resend.setOnClickListener {
            job?.cancel()
            binding.input1.text?.clear()
            binding.input2.text?.clear()
            binding.input3.text?.clear()
            binding.input4.text?.clear()
            binding.input5.text?.clear()
            binding.input1.focusable
            binding.resend.visibility = View.GONE
            binding.timer.visibility = View.VISIBLE
            binding.btnOtp.visibility = View.VISIBLE
            i = 121
            job = lifecycleScope.launch {
                viewModel.sendEmail()
                setMyProgressBar(1300L)
            }


        }
    }

    override fun onBackPressed() {
        job?.cancel()
        when (viewModel.getScreenNumber()) {
            1 -> {
                LoginActivity.startWithFinish(this@ForgotPasswordActivity)
            }

            2 -> {
                viewModel.setScreenNumber(1)
            }

            3 -> {
                viewModel.setScreenNumber(2)
            }

            4 -> {
                viewModel.setScreenNumber(3)
            }

        }

    }


    override fun getViewBinding(): ActivityForgotPasswordBinding =
        ActivityForgotPasswordBinding.inflate(layoutInflater)

    override fun observer() {
        viewModel.currentScreen.observe(this) {
            when (it) {
                1 -> {
                    binding.topBar.heading.text = getString(R.string.forgot_password)
                    binding.forgotPassword.visibility = View.VISIBLE
                    binding.otp.visibility = View.GONE
                    binding.resetPassword.visibility = View.GONE
                }

                2 -> {
                    binding.topBar.heading.text = getString(R.string.forgot_password)
                    binding.input1.requestFocus()
                    binding.forgotPassword.visibility = View.GONE
                    binding.otp.visibility = View.VISIBLE
                    binding.resetPassword.visibility = View.GONE
                    i = 121
                    job = lifecycleScope.launch {
                        setMyProgressBar(1000L)
                    }
                }

                3 -> {
                    binding.topBar.heading.text = getString(R.string.reset_password)
                    binding.forgotPassword.visibility = View.GONE
                    binding.otp.visibility = View.GONE
                    binding.resetPassword.visibility = View.VISIBLE
                }

                4 -> {
                    LoginActivity.startWithFinish(this@ForgotPasswordActivity)

                }
            }
        }

        viewModel.forgotResponse.observe(this) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    if (it.message == "401") {
                        LanguageActivity.start(this)
                    }else {
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.message!!,
                            this@ForgotPasswordActivity,
                            language
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true
                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false
                    id = it.data?._id!!
                    viewModel.setScreenNumber(2)
                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (language == "Japanese" || language == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            this@ForgotPasswordActivity,
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            this@ForgotPasswordActivity,
                            language
                        )
                }
            }

        }

        viewModel.otpResponse.observe(this) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    if (it.message == "401") {
                        LanguageActivity.start(this)
                    }else {
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.message!!,
                            this@ForgotPasswordActivity,
                            language
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true
                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false
                    if (it.data?.status == "200") {
                        viewModel.setScreenNumber(3)
                    } else {
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.data?.message!!,
                            this@ForgotPasswordActivity,
                            language
                        )
                    }

                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (language == "Japanese" || language == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            this@ForgotPasswordActivity,
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            this@ForgotPasswordActivity,
                            language
                        )
                }
            }

        }

        viewModel.resetPasswordResponse.observe(this) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    if (it.message == "401") {
                        LanguageActivity.start(this)
                    } else {
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.message!!,
                            this@ForgotPasswordActivity,
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
                        LocalData(this@ForgotPasswordActivity).clearSaveCredentials()
                        cancel()
                    }
                    LoginActivity.startWithFinish(this)
                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (language == "Japanese" || language == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            this@ForgotPasswordActivity,
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            this@ForgotPasswordActivity,
                            language
                        )
                }
            }

        }

        viewModel.validationResponse.observe(this) {
            ShowSnackBar.showBar(binding.frameLayout, it, this@ForgotPasswordActivity, language)
        }
    }

    private suspend fun setMyProgressBar(delay: Long) {
        do {
            i--
            withContext(Dispatchers.Main) {
                binding.timer.text =
                    SimpleDateFormat("HH:mm").apply { timeZone = TimeZone.getTimeZone("UTC") }
                        .format(Date(TimeUnit.MINUTES.toMillis(i.toLong())))

            }
            delay(delay)
        } while (i >= 1)
        withContext(Dispatchers.Main) {
            binding.resend.visibility = View.VISIBLE
            binding.timer.visibility = View.GONE
            binding.btnOtp.visibility = View.GONE
        }
    }

}