package com.app.yoursafetyfirst.ui.prevoiusdata

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.DriverSafetyApp
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.ActivityPreviousDataDetailBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.ui.language.LanguageActivity
import com.app.yoursafetyfirst.utils.DateConversion
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PreviousDataDetailActivity : BaseActivity<ActivityPreviousDataDetailBinding>() {

    companion object {
        private const val DECLARATIONID: String = "declarationId"
        private const val ID: String = "id"


        @JvmStatic
        fun start(context: Context, declarationId: String, _id: String) {
            val starter = Intent(context, PreviousDataDetailActivity::class.java)
            starter.putExtra(DECLARATIONID, declarationId)
            starter.putExtra(ID, _id)
            context.startActivity(starter)
        }
    }

    var declarationId: String = ""
    var id: String = ""
    val viewModel: PreviousDataViewModel by viewModels()
    lateinit var language: String

    override fun onCreate() {

        declarationId = intent.getStringExtra(DECLARATIONID).toString()
        id = intent.getStringExtra(ID).toString()

        if (checkForInternet(this@PreviousDataDetailActivity)) {
            lifecycleScope.launch {
                LocalData(this@PreviousDataDetailActivity).token.first().let {
                    viewModel.getSingleDiagnostic(it, declarationId, id)
                }
            }
        } else {
            ShowSnackBar.showBar(
                binding.frameLayout,
                R.string.no_internt,
                this@PreviousDataDetailActivity,
                DriverSafetyApp.selectedLanguage
            )
        }


        lifecycleScope.launch {
            LocalData(this@PreviousDataDetailActivity).language.first().let {
                language = it
            }
        }

        binding.topBar.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.topBar.heading.text = getString(R.string.diagnosis_health_history)
    }

    override fun getViewBinding() =
        ActivityPreviousDataDetailBinding.inflate(layoutInflater)

    override fun observer() {
        viewModel.singleDataResponse.observe(this) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    if (it.message == "401") {
                        LanguageActivity.start(this)
                    } else {
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.message!!,
                            this@PreviousDataDetailActivity,
                            language
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true
                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false
                    binding.singlePreviousData = it.data


                    if (language == "en") {
                        binding.title.text = it.data?.finalJudgement?.get(0)?.title?.en
                        binding.level.text = it.data?.finalJudgement?.get(0)?.level?.en
                        binding.description.text = it.data?.finalJudgement?.get(0)?.description?.en
                    } else {
                        binding.title.text = it.data?.finalJudgement?.get(0)?.title?.ja
                        binding.level.text = it.data?.finalJudgement?.get(0)?.level?.ja
                        binding.description.text = it.data?.finalJudgement?.get(0)?.description?.ja

                    }


                    if (it.data?.observation?.deviceType.equals("camera", true)) {
                        binding.deviceType.text = resources.getString(R.string.camera)
                    } else {
                        binding.deviceType.text = resources.getString(R.string.ring)

                    }

                    binding.excersice.text = it.data?.observation?.exerciseScore?.toInt().toString()
                    binding.reflectTest.text =
                        it.data?.reflexaction?.time + " " + getString(R.string.second)

                    /* if(it.data?.observation?.deviceType =="RING") {
                         binding.from.text =resources.getString(R.string.data_acquisition_camera_smart_ring)
                     }else{
                         binding.from.text =resources.getString(R.string.data_acquisition_camera)
                     }*/

                    val dateStr: String =
                        DateConversion.utcConversion(it.data?.finalJudgement?.get(0)?.createdAt.toString())
                    val dateArr = dateStr.split(" ")
                    binding.dateOnly.text = dateStr
                    binding.time.text = dateArr[2]


                    /*val gradientInsta = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(
                            Color.parseColor(it.data?.finalJudgement?.get(0)?.colorCode),
                            Color.parseColor(it.data?.finalJudgement?.get(0)?.colorCode),
                            ContextCompat.getColor(this@PreviousDataDetailActivity, R.color.white)
                        )
                    )
                    findViewById<View>(R.id.card_view).background = gradientInsta*/

                    binding.cardView.setBackgroundColor(
                        Color.parseColor(
                            it.data?.finalJudgement?.get(0)?.colorCode
                        )
                    )
                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (DriverSafetyApp.selectedLanguage == "Japanese" || DriverSafetyApp.selectedLanguage == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            this@PreviousDataDetailActivity,
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            this@PreviousDataDetailActivity,
                            language
                        )

                }
            }

        }
    }
}