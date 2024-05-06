package com.app.yoursafetyfirst.ui.camerapulserate

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.DriverSafetyApp
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.ActivityPulseOnGraphBinding
import com.app.yoursafetyfirst.repository.NetworkResult
import com.app.yoursafetyfirst.ui.camerapulserate.PulseFromCameraActivity.Companion.graphValue
import com.app.yoursafetyfirst.ui.language.LanguageActivity
import com.app.yoursafetyfirst.ui.physicalcondition.DataConfirmationActivity
import com.app.yoursafetyfirst.ui.ring.QolResultActivity
import com.app.yoursafetyfirst.utils.Constants
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.ShowSnackBar
import com.app.yoursafetyfirst.utils.checkForInternet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PulseOnGraphActivity : BaseActivity<ActivityPulseOnGraphBinding>() {


    private lateinit var pulseCount: String
    var id: String? = null

    val viewModel: CameraViewModel by viewModels()

    var stepCount: String = "0"
    var todayCount: String = "0"
    var date: String = ""
    var language: String = ""

//    private val stepCountReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            Log.e("100", "todayCount Pluse: $todayCount")
//            todayCount = intent.getStringExtra("stepCount").toString()
//            lifecycleScope.launch {
//                LocalData(this@PulseOnGraphActivity).storeTodayStepCount(todayCount.toInt())
//            }
//            date = intent.getStringExtra("date").toString()
//            Log.e("100", "PulseOnGraphActivity stepCount: $todayCount")
//            Log.e("100", "PulseOnGraphActivity date: $date")
//        }
//    }

    companion object {
        private const val PHYSICAL_ID = "ID"

        @JvmStatic
        fun start(context: Activity, pulseCount: String, id: String?) {
            val starter = Intent(context, PulseOnGraphActivity::class.java)
            starter.putExtra("pulse_count", pulseCount)
            starter.putExtra(PHYSICAL_ID, id)
            context.finish()
            context.startActivity(starter)

        }
    }


    override fun onCreate() {

        pulseCount = intent.getStringExtra("pulse_count")!!
        id = intent.getStringExtra(PHYSICAL_ID)

        binding.topBar.heading.text = getString(R.string.sdnc_diagnosis)

        // Register the broadcast receiver
      //  val filter = IntentFilter("com.app.driversafety.STEP_COUNT_UPDATE")
      //  registerReceiver(stepCountReceiver, filter)

        //stepCounter.authenticateGoogleLogin()
//
//        lifecycleScope.launch {
//            LocalData(this@PulseOnGraphActivity).todayStepCount.first().let {
//                //stepCount = it.toString()
//                todayCount = it.toString()
//                Log.e("11", "stepCount: $stepCount")
//            }
//        }

        lifecycleScope.launch {
            LocalData(this@PulseOnGraphActivity).language.first().let {
                language = it
            }
        }

        binding.pulseCount.text = pulseCount

        if ((graphValue.isNotEmpty())) {
            binding.textureView2.postDelayed({
                ChartDrawer(binding.textureView2).draw(graphValue)
            }, 1000)

        }

        binding.next.setOnClickListener {
            lifecycleScope.launch {
                val todayStep = LocalData(this@PulseOnGraphActivity).todayStepCount.first().let {
                    it
                }
                if (checkForInternet(this@PulseOnGraphActivity)) {
                    lifecycleScope.launch {
                        val measurementArray = ArrayList<String>()
                        val timeArray = ArrayList<String>()
                        for (a in 0..<graphValue.size) {
                            if (!timeArray.contains("${graphValue[a].timestamp}")) {
                                timeArray.add("${graphValue[a].timestamp}")
                                measurementArray.add(("${(graphValue[a].measurement * 100).toInt()}"))
                            }
                        }
                        LocalData(this@PulseOnGraphActivity).token.first().let {
                            viewModel.addObservation(
                                it,
                                id,
                                pulseCount,
                                "CAMERA",
                                Constants.getFootSteps().toString(),
                                "",
                                measurementArray,
                                timeArray
                            )
                        }
                    }
                } else {
                    ShowSnackBar.showBar(
                        binding.frameLayout,
                        R.string.no_internt,
                        this@PulseOnGraphActivity,
                        DriverSafetyApp.selectedLanguage
                    )
                }


            }
        }

        binding.topBar.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun getViewBinding(): ActivityPulseOnGraphBinding =
        ActivityPulseOnGraphBinding.inflate(layoutInflater)

    override fun observer() {
        viewModel.observationResponse.observe(this) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progress.progressBar = false
                    if (it.message == "401") {
                        lifecycleScope.launch {
                            LocalData(this@PulseOnGraphActivity).clearDataStore()
                            LanguageActivity.start(this@PulseOnGraphActivity)
                        }
                    } else {
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.message!!,
                            this@PulseOnGraphActivity,
                            language
                        )
                    }
                }

                is NetworkResult.Loading -> {
                    binding.progress.progressBar = true
                }

                is NetworkResult.Success -> {
                    binding.progress.progressBar = false
                    QolResultActivity.start(
                        this,
                        it.data?.QOLResult,
                        it.data?.observationData?.declarationId,
                        it.data?.observationData?.stressScore
                    )

                }

                is NetworkResult.Validation -> {
                    binding.progress.progressBar = false
                    if (language == "Japanese" || language == "ja")
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.ja!!,
                            this@PulseOnGraphActivity,
                            language
                        )
                    else
                        ShowSnackBar.showBarString(
                            binding.frameLayout,
                            it.validationMessage?.en!!,
                            this@PulseOnGraphActivity,
                            language
                        )
                }
            }

        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        DataConfirmationActivity.startFinish(this)
    }


}


