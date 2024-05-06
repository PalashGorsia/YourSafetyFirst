package com.app.yoursafetyfirst.ui.login

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.ActivityLoginBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.ui.MainActivity
import com.app.yoursafetyfirst.ui.forgotpassword.ForgotPasswordActivity
import com.app.yoursafetyfirst.ui.profile.ChangePasswordActivity
import com.app.yoursafetyfirst.ui.signup.SignupActivity
import com.app.yoursafetyfirst.utils.HideKeyboard
import com.app.yoursafetyfirst.utils.Language
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.LocaleHelper
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    companion object {
        @JvmStatic
        fun start(context: Activity) {
            val starter = Intent(context, LoginActivity::class.java)
            context.startActivity(starter)
        }

        @JvmStatic
        fun startWithFinish(context: Activity) {
            val starter = Intent(context, LoginActivity::class.java)
            context.startActivity(starter)
            context.finishAffinity()
        }
    }

    private val loginViewModel: LoginViewModel by viewModels()
    var language: String = ""
    var saveCredentials: Boolean = false
    var token = ""

    override fun onCreate() {
        binding.viewModel = loginViewModel

        lifecycleScope.launch {
            LocalData(this@LoginActivity).language.first().let {
                language = if (it == "en") {
                    Language.English().x
                } else {
                    Language.Japanese().x
                }
            }
        }

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener<String?> { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                token = task.result


            })

        binding.appBar.heading.text = getString(R.string.login)


        binding.sigup.setOnClickListener {
            SignupActivity.start(this@LoginActivity)
        }

        binding.login.setOnClickListener {
            if (checkForInternet(this@LoginActivity)) {
                lifecycleScope.launch {
                    HideKeyboard.hideKeyboard(this@LoginActivity, binding.frameLayout)
                    loginViewModel.login(
                        language,
                        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID),
                        token
                    )
                }
            } else {
                ShowSnackBar.showBar(
                    binding.frameLayout,
                    R.string.no_internt,
                    this@LoginActivity,
                    language
                )
            }
        }

        binding.forgotPassword.setOnClickListener {
            ForgotPasswordActivity.start(this@LoginActivity)
        }

        binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            saveCredentials = isChecked
            lifecycleScope.launch {
                LocalData(this@LoginActivity).saveCredentials(saveCredentials)
            }
        }


        lifecycleScope.launch {
            LocalData(this@LoginActivity).getSavedCredentials.first().let {
                if (it) {
                    binding.checkbox.isChecked = it

                    lifecycleScope.launch {
                        LocalData(this@LoginActivity).email.first().let { email ->
                            loginViewModel.emailObserver.set(email)
                        }
                    }

                    lifecycleScope.launch {
                        LocalData(this@LoginActivity).password.first().let { password ->
                            loginViewModel.passwordObserver.set(password)
                        }
                    }


                }
            }
        }
    }

    override fun getViewBinding(): ActivityLoginBinding =
        ActivityLoginBinding.inflate(layoutInflater)

    override fun observer() {
        loginViewModel.signInResponse.observe(this) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    ShowSnackBar.showBarString(
                        binding.frameLayout,
                        it.message!!,
                        this@LoginActivity,
                        language
                    )
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true
                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false

                    if (it.data!!.resetPassword) {
                        lifecycleScope.launch {
                            LocalData(this@LoginActivity).storeAccessToken(it.data.loginToken)
                            cancel()
                        }
                        ChangePasswordActivity.start(this)
                    } else {
                        lifecycleScope.launch {
                            LocalData(this@LoginActivity).storeUserInfo(
                                it.data?.email!!,
                                it.data.name,
                                it.data._id,
                                it.data.loginToken,
                                it.data.ringUse
                            )

                            if (saveCredentials) {
                                loginViewModel.passwordObserver.get().let { password ->
                                    LocalData(this@LoginActivity).storePassword(password!!)
                                }
                            }


                            if (it.data.language == "English") {
                                LocaleHelper.setLocale(this@LoginActivity, "en")
                            } else {
                                LocaleHelper.setLocale(this@LoginActivity, "ja")
                            }
                        }

                        MainActivity.start(this)
                    }


                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (language == "Japanese" || language == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            this@LoginActivity,
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            this@LoginActivity,
                            language
                        )
                }
            }

        }

        loginViewModel.validationResponse.observe(this) {
            ShowSnackBar.showBar(binding.frameLayout, it, this@LoginActivity, language)
        }
    }

}