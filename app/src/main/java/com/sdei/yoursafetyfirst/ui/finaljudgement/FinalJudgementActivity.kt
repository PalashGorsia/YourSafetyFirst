package com.app.yoursafetyfirst.ui.finaljudgement

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.DriverSafetyApp
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.ActivityFinalJudgementBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.ui.language.LanguageActivity
import com.app.yoursafetyfirst.utils.Constants
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FinalJudgementActivity : BaseActivity<ActivityFinalJudgementBinding>() {

    companion object {
        @JvmStatic
        fun start(context: Context, id: String) {
            val starter = Intent(context, FinalJudgementActivity::class.java)
            starter.putExtra(Constants.ID, id)
            context.startActivity(starter)
        }
    }

    val viewModel: FinalJudgementViewmodel by viewModels()
    lateinit var id: String
    lateinit var language: String

    override fun onCreate() {
        id = intent.getStringExtra(Constants.ID)!!
        binding.topBar.heading.text = getString(R.string.sdnc_diagnosis)

        lifecycleScope.launch {
            LocalData(this@FinalJudgementActivity).language.first().let {
                language = it
            }
        }

        if (checkForInternet(this@FinalJudgementActivity)) {
            lifecycleScope.launch {
                LocalData(this@FinalJudgementActivity).token.first().let {
                    viewModel.getDiagnostic(it, id)
                }
            }
        } else {
            ShowSnackBar.showBar(
                binding.frameLayout,
                R.string.no_internt,
                this@FinalJudgementActivity,
                DriverSafetyApp.selectedLanguage
            )
        }


        binding.btnRegistration.setOnClickListener { RegistrationCompleteActivity.start(this) }

        binding.topBar.imgBack.setOnClickListener { onBackPressed() }
    }

    override fun getViewBinding(): ActivityFinalJudgementBinding =
        ActivityFinalJudgementBinding.inflate(layoutInflater)

    override fun observer() {
        viewModel.judgementResponse.observe(this) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.showBlankView = true
                    binding.progress.progressBar = false
                    if (it.message == "401") {
                        LanguageActivity.start(this)
                    } else {
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.message!!,
                            this@FinalJudgementActivity,
                            language
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    binding.showBlankView = true
                    binding.progress.progressBar = true
                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false
                    binding.showBlankView = false

                    /*binding.cardView.background = (getDrawable(R.drawable.black_outer_tenradius) as ColorDrawable).apply {
                        Color.parseColor(it.data!!.finalJudgement[0].colorCode)
                    }*/

                    (binding.cardView).setBackgroundColor(Color.parseColor(it.data!!.finalJudgement[0].colorCode))

                    /*val gradientInsta = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(
                            Color.parseColor(it.data?.finalJudgement?.get(0)?.colorCode),
                            Color.parseColor(it.data?.finalJudgement?.get(0)?.colorCode),
                            ContextCompat.getColor(this@FinalJudgementActivity, R.color.white)
                        )
                    )
                    findViewById<View>(R.id.card_view).background = gradientInsta*/


                    it.data?.let { data ->
                        if (language == "en") {
                            binding.title.text = data.finalJudgement[0].title.en
                            binding.level.text = data.finalJudgement[0].level.en
                            binding.description.text = data.finalJudgement[0].description.en

                        } else {
                            binding.title.text = data.finalJudgement[0].title.ja
                            binding.level.text = data.finalJudgement[0].level.ja
                            binding.description.text = data.finalJudgement[0].description.ja
                        }
                    }
                }

                is NetworkResult.Validation -> {
                    binding.showBlankView = true
                    binding.progress.progressBar = false
                    if (language == "Japanese" || language == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            this@FinalJudgementActivity,
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            this@FinalJudgementActivity,
                            language
                        )

                }
            }
        }
    }
}