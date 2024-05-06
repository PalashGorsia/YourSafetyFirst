package com.app.yoursafetyfirst.ui.camerapulserate

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Surface
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.ActivityPulseFromCameraBinding
import com.app.yoursafetyfirst.utils.Constants
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.ShowSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.roundToInt

@AndroidEntryPoint
class PulseFromCameraActivity : BaseActivity<ActivityPulseFromCameraBinding>() {
    lateinit var permissionController: PermissionController
    var requestPermissionsResult: ActivityResultLauncher<Set<Permission>>? = null
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(this) }

    companion object {
        const val MESSAGE_UPDATE_REALTIME = 1
        const val MESSAGE_UPDATE_FINAL = 2
        const val MESSAGE_CAMERA_NOT_AVAILABLE = 3

        var graphValue = CopyOnWriteArrayList<Measurement<Float>>()
        var txt: String = "0"
        private const val PHYSICAL_ID = "ID"

        @JvmStatic
        fun start(context: Context, id: String?) {
            val starter = Intent(context, PulseFromCameraActivity::class.java)
            starter.putExtra(PHYSICAL_ID, id)
            context.startActivity(starter)
        }
    }


    var id: String? = null
    private var analyzer: OutputAnalyzer? = null
    private var previewSurfaceTexture: SurfaceTexture? = null

    @SuppressLint("HandlerLeak")
    private val mainHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // Log.e("OutputAnalyzer", "PulseFromCameraActivity   $msg")

            if (msg.what == MESSAGE_UPDATE_REALTIME && msg.arg1 == 1) {
                txt = msg.obj.toString().split("Pulse: ")[1].split(" ")[0]
                (findViewById<View>(R.id.pulse_count) as TextView).text =
                    (txt.toFloat().roundToInt()).toString()
                Log.e("Pulse rate", "handleMessage: ${txt.toFloat().roundToInt()}")
            }
            if (msg.what == MESSAGE_CAMERA_NOT_AVAILABLE) {
                analyzer!!.stop()
            }
        }
    }

    private val cameraService = CameraService(this, mainHandler)
    private var language: String = ""

    override fun onCreate() {
        analyzer = OutputAnalyzer(this, binding.textureView2, mainHandler)
        id = intent.getStringExtra(PHYSICAL_ID)

        binding.topBar.heading.text = getString(R.string.sdnc_diagnosis)

        lifecycleScope.launch {
            LocalData(this@PulseFromCameraActivity).language.first().let {
                if (it.isNotEmpty()) {
                    language = it
                }
            }
        }

        binding.topBar.imgBack.setOnClickListener {
            onBackPressed()
        }

        try {
            permissionController = healthConnectClient.permissionController
            requestPermissionsResult =
                registerForActivityResult(permissionController.createRequestPermissionActivityContract()) { granted ->
                    Log.d(TAG, "checkisHealthConnectAvailable: called onpermission")
                    onPermissionResult(granted)
                }
        } catch (e: Exception) {
            Log.d(TAG, "onCreate: excp ${e.message}")
        }

    }

    val ALL_PERMISSIONS = setOf(
        Permission.createReadPermission(StepsRecord::class),
        Permission.createWritePermission(StepsRecord::class),
    )

    private fun onPermissionResult(granted: Set<androidx.health.connect.client.permission.Permission>?) {
        if (granted?.containsAll(ALL_PERMISSIONS) == true) { // Permissions successfully granted
            //startMainActivity()
            proceedForJob()
            Log.d("TAG", "checkConnected: permission allowed onper")
        } else {

            Log.d("TAG", "checkConnected: permission not allowed onper")
            Toast.makeText(
                this,
                getString(R.string.healthcarePermissionNotGranted),
                Toast.LENGTH_SHORT
            ).show()
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 1500)

        }

    }


    private fun proceedForJob() {

        lifecycleScope.launch {
            readAggregatedStepsLast30Days()
            Log.d("TAG", "proceedForJob: proceed called")
//            readStepsOfLast30DaysAllEntry()
//            readAggregatedCalories()
        }
    }


    private val TAG = "tagg"


    var formatter1 = DateTimeFormatter.ofPattern("dd MM yyyy")
    private suspend fun readAggregatedStepsLast30Days() {
        val startTime = LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS)
        val endTime = LocalDateTime.now().plusMinutes(5)
        Log.e("Request_Steps", startTime.toString() + "  " + endTime.toString())
        Log.e("Request_Steps", "requeststeps" + startTime.toString() + "  " + endTime.toString())

        val response = healthConnectClient.aggregateGroupByPeriod(
            AggregateGroupByPeriodRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                timeRangeSlicer = Period.ofDays(1)
            )
        )
        try {
            var text = ""
            var text1 = " data ${response.size}"
            Log.d("TAG", "readAggregatedStepsLast30Days: text1 ${text1.toString()}")
            for (stepRecord in response) {
                val step = stepRecord.result[StepsRecord.COUNT_TOTAL] ?: 0
                Log.e(
                    "TAG",
                    ">timed " + stepRecord.startTime + " :" + stepRecord.endTime + " :>" + step
                )
                val date = formatter1.format(stepRecord.startTime)
//            // Process each step record
                text = step.toString()
            }
            try {
                if (text == "") {
                    Constants.setFootSteps(0)
                    Log.d(TAG, "readAggregatedStepsLast30Days: steps if : $text")
                } else {
                    Constants.setFootSteps(text.toInt())
                    Log.d(TAG, "readAggregatedStepsLast30Days: steps else : $text")
                }

                Log.d("TAG", "readAggregatedStepsLast30Days: total count $text")
            } catch (e: Exception) {
                Log.d(TAG, "readAggregatedStepsLast30Days: excpep ${e.message}")
            }


        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("TAG", "readAggregatedStepsLast30Days: excep ${e.message}")
        }
    }


    override fun getViewBinding(): ActivityPulseFromCameraBinding =
        ActivityPulseFromCameraBinding.inflate(layoutInflater)

    override fun observer() {

        analyzer?.progressResponse?.observe(this) {
            binding.progressBar.progress = it
            binding.progressText.text = "$it%"
            if (it == 100) {
                graphValue = analyzer?.plotGraph()!!
                PulseOnGraphActivity.start(this, txt.toFloat().roundToInt().toString(), id)
            }
        }
    }

    private fun isHealthConnectAppAvailable() = HealthConnectClient.isAvailable(this)
    override fun onResume() {
        super.onResume()
        if (isHealthConnectAppAvailable()) {
            checkisHealthConnectAvailable()
        } else {
            Log.d(TAG, "onResume: else part is available")
        }
    }


    private fun checkisHealthConnectAvailable() {
        if (HealthConnectClient.isAvailable(this)) {
            // Health Connect is available
            Log.d(TAG, "checkisHealthConnectAvailable: available.")
            permissionController = healthConnectClient.permissionController
            checkConnected(permissionController)
            // startCameraMeasure()
        } else {
            //health connect app is not installed
            Log.d(TAG, "checkisHealthConnectAvailable: not available.")
            // showDialogfordownloadingHealthCare()
            runOnUiThread {
                Toast.makeText(
                    this,
                    getString(R.string.txt_app_is_notInstalled),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkConnected(permissionController: PermissionController) {
        lifecycleScope.launch {
            val grantedPermissions = permissionController.getGrantedPermissions(ALL_PERMISSIONS)
            Log.d("TAG", "checkConnected: permission not allowed size ${grantedPermissions.size}")
            if (grantedPermissions.size == 0) { //no permission granted
//                // findViewById<LinearLayout>(R.id.llError).visibility = View.VISIBLE
//                runOnUiThread {
//                    // requestPermissionsResult?.launch(ALL_PERMISSIONS)
//                }
                if (requestPermissionsResult == null) {
                    Log.d(TAG, "checkConnected: requ permi null")
//                    //initializePermission()
//                    runOnUiThread {
//                        // requestPermissionsResult?.launch(ALL_PERMISSIONS)
//                    }
                } else {
                    if (!isDialogOpened)
                        showDialogfordownloadingHealthCare()
                    isDialogOpened = true

                    Log.d(TAG, "checkConnected: requ permi else")
                }
                Log.d("TAG", "checkConnected: permission not allowed")
            } else { //Permissions allowed do your job
                //startMainActivity()
                Log.d("TAG", "checkConnected: permission allowed")
                startCameraMeasure()
            }
        }
    }

    private fun startCameraMeasure() {
        binding.imagePlaceholder.visibility = View.VISIBLE
        lifecycleScope.launch {
            delay(3000L)
            binding.imagePlaceholder.visibility = View.GONE
            previewSurfaceTexture = binding.textureView2.surfaceTexture
            if (previewSurfaceTexture != null) {
                val previewSurface = Surface(previewSurfaceTexture)

                // show warning when there is no flash
                if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    ShowSnackBar.showBarString(
                        binding.frameLayout,
                        getString(R.string.noFlashWarning),
                        this@PulseFromCameraActivity,
                        language
                    )
                }

                cameraService.start(previewSurface)
                analyzer!!.measurePulse(binding.textureView2, cameraService)
            }

        }


    }

    private var isDialogOpened = false

    fun showDialogfordownloadingHealthCare() {
        val dialog = AlertDialog.Builder(this)
        dialog.setPositiveButton(getString(R.string.ok), object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                runOnUiThread {
                    requestPermissionsResult?.launch(ALL_PERMISSIONS)
                }
            }
        })
        dialog.setOnDismissListener {

        }
        dialog.setMessage(getString(R.string.txt_GrantThePermissionForHealthConnectFirst))
        dialog.setCancelable(false)
        dialog.create()
        dialog.show()
    }


    override fun onPause() {
        super.onPause()
        cameraService.stop()
        if (analyzer != null)
            analyzer!!.stop()
    }


    /* override fun onBackPressed() {
         CameraActivity.startFinish(this, id!!)
     }*/


}