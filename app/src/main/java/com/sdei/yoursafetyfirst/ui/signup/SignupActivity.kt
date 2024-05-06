package com.app.yoursafetyfirst.ui.signup

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.ActivitySignupBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.ui.login.LoginActivity
import com.app.yoursafetyfirst.utils.Constants.isValidPassword
import com.app.yoursafetyfirst.utils.CustomSpinnerAdapter
import com.app.yoursafetyfirst.utils.HideKeyboard
import com.app.yoursafetyfirst.utils.Language
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.LocaleHelper
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.YearPickerDialog
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

@AndroidEntryPoint
class SignupActivity : BaseActivity<ActivitySignupBinding>() {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, SignupActivity::class.java)
            context.startActivity(starter)
        }
    }

    private val viewModel: SignupViewModel by viewModels()
    lateinit var language: String
    override fun onCreate() {
        binding.viewModel = viewModel

        lifecycleScope.launch {
            LocalData(this@SignupActivity).language.first().let {
                if (it == "en") {
                    language = Language.English().x
                } else {
                    language = Language.Japanese().x

                }
            }

        }

        binding.appBar.heading.text = getString(R.string.signup)


        binding.genderSpinner.adapter =
            CustomSpinnerAdapter(this@SignupActivity, resources.getStringArray(R.array.Gender))


        binding.login.setOnClickListener {
            LoginActivity.startWithFinish(this)
        }

        binding.signup.setOnClickListener {
            if (checkForInternet(this@SignupActivity)) {
                lifecycleScope.launch {
                    HideKeyboard.hideKeyboard(this@SignupActivity, binding.frameLayout)
                    viewModel.registerUser(language)
                }
            } else {
                ShowSnackBar.showBar(
                    binding.frameLayout,
                    R.string.no_internt,
                    this@SignupActivity,
                    language
                )
            }

        }

        binding.birthYear.setOnClickListener {
            YearPickerDialog(Date()).apply {
                setListener { view, year, month, dayOfMonth ->
                    viewModel.yearObserver.set(year.toString())
                }
            }.show(supportFragmentManager, "MonthYearPickerDialog")
        }

        binding.ringUse.setOnCheckedChangeListener { _, checkedId ->
            viewModel.ringClickedObserver.set(true)
            val radio: RadioButton = binding.root.findViewById(checkedId)
            if (radio.text.toString().equals("yes", true) || radio.text.toString()
                    .equals("はい", true)
            ) {
                binding.ring = true
                viewModel.ringObserver.set(binding.ring)
            } else {
                binding.ring = false
                viewModel.ringObserver.set(binding.ring)
                binding.ringId.text!!.clear()
            }
        }

        binding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                //val genderArray = resources.getStringArray(R.array.Gender)
                val genderArray = arrayOf("Select gender", "Male", "Female", "Other")
                viewModel.genderObserver.set(genderArray[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }


        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed for this example
            }

            override fun afterTextChanged(s: Editable?) {
                if (!isValidPassword(s.toString().trim())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        binding.passwordInput.cursorErrorColor =
                            resources.getColorStateList(R.color.black, null)
                    }
                    binding.passwordInput.error = resources.getString(R.string.weak_password)
                } else {
                    binding.passwordInput.error = null
                }
                viewModel.passwordObserver.set(s.toString().trim())

            }
        })
    }

    override fun getViewBinding(): ActivitySignupBinding =
        ActivitySignupBinding.inflate(layoutInflater)

    override fun observer() {
        viewModel.registerResponse.observe(this) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    ShowSnackBar.showBarString(binding.frameLayout, it.message!!, this@SignupActivity, language)
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true
                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false
                    lifecycleScope.launch {
                        /*LocalData(this@SignupActivity).storeUserInfo(
                            it.data?.email!!,
                            it.data.name,
                            it.data._id,
                            it.data.loginToken,
                            it.data.ringUse
                        )*/

                        if (it.data?.language == "English") {
                            LocaleHelper.setLocale(this@SignupActivity, "en")
                        } else {
                            LocaleHelper.setLocale(this@SignupActivity, "ja")
                        }
                        LoginActivity.startWithFinish(this@SignupActivity)
                        cancel()
                    }

                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (language == "Japanese" || language == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            this@SignupActivity,
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            this@SignupActivity,
                            language
                        )

                }
            }

        }

        viewModel.validationResponse.observe(this) {
            ShowSnackBar.showBar(binding.frameLayout, it, this@SignupActivity, language)
        }
    }
}