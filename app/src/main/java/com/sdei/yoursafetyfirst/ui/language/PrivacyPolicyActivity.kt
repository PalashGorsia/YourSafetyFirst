package com.app.yoursafetyfirst.ui.language

import android.content.Context
import android.content.Intent
import android.text.method.ScrollingMovementMethod
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.ActivityPrivacyPolicyBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.ui.login.LoginActivity
import com.app.yoursafetyfirst.utils.Constants
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PrivacyPolicyActivity : BaseActivity<ActivityPrivacyPolicyBinding>() {

    companion object {
        @JvmStatic
        fun start(context: Context, language: String) {
            val starter = Intent(context, PrivacyPolicyActivity::class.java)
            starter.putExtra(Constants.LANGUAGE, language)
            context.startActivity(starter)
        }
    }

    private val viewModel: LanguageViewModel by viewModels()
    lateinit var language: String
    override fun onCreate() {

        language = intent.getStringExtra(Constants.LANGUAGE)!!

        if (checkForInternet(this@PrivacyPolicyActivity)) {
            lifecycleScope.launch {
                LocalData(this@PrivacyPolicyActivity).token.first().let {
                    viewModel.getAllPolicy(it)
                }
            }
        } else {
            ShowSnackBar.showBar(
                binding.frameLayout,
                R.string.no_internt,
                this@PrivacyPolicyActivity,
                language
            )
        }


        binding.save.setOnClickListener {
            if (binding.checkbox.isChecked) {
                lifecycleScope.launch {
                    LocalData(this@PrivacyPolicyActivity).storePrivacyPolicy("privacy")
                }
                LoginActivity.start(this)
            } else {
                ShowSnackBar.showBar(
                    binding.frameLayout,
                    R.string.accept_privacy,
                    this@PrivacyPolicyActivity,
                    language
                )

            }
        }
    }


    override fun getViewBinding() = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
    override fun observer() {
        viewModel.allPolicyResponse.observe(this) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    if (it.message == "401") {
                        lifecycleScope.launch {
                            LocalData(this@PrivacyPolicyActivity).clearDataStore()
                            LanguageActivity.start(this@PrivacyPolicyActivity)
                        }
                    } else {
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.message!!,
                            this@PrivacyPolicyActivity,
                            language
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true

                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false
                    if (language == "en") {
                        binding.description.text = it.data?.get(0)?.description!!.en
                        binding.topBar.heading.text = (it.data[0].title.en)

                        binding.description.movementMethod = ScrollingMovementMethod()

                    } else {
                        binding.description.text = (it.data?.get(0)?.description!!.ja)
                        binding.topBar.heading.text = (it.data[0].title.ja)
                        binding.description.movementMethod = ScrollingMovementMethod()

                    }

                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (language == "Japanese" || language == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            this@PrivacyPolicyActivity,
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            this@PrivacyPolicyActivity,
                            language
                        )

                }
            }
        }

    }
}