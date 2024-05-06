package com.app.yoursafetyfirst.utils.stepcount

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.app.yoursafetyfirst.utils.LocalData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class StepDetectorService : Service(), SensorEventListener {
    private var oldStepCount = 0
    private var stepCount = 0
    private  var todayStepCount :Int=0
    private var date = ""
    private lateinit var handler: Handler
    private lateinit var sensorManager: SensorManager
    private var countSensor: Sensor? = null


    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL)

            GeneralHelper.updateNotification(
                this@StepDetectorService,
                this@StepDetectorService
            )

        } else {
            Toast.makeText(this, "Sensor Not Detected", Toast.LENGTH_SHORT).show()
        }

        // Initialize handler
        handler = Handler()
        val myRunnable = object : Runnable {
            override fun run() {
                scope.launch {
                    LocalData(this@StepDetectorService).currentDate.collect {
                        if (it != GeneralHelper.getToadyDate()) {
                            LocalData(this@StepDetectorService).storeTotalStepCount(stepCount)
                            LocalData(this@StepDetectorService).storeCurrentDate(GeneralHelper.getToadyDate())
                            Log.e("TAG", "stepCount diff date: $stepCount", )
                        } else {
                            LocalData(this@StepDetectorService).totalStepCount.collect { step ->
                                val todayCount = stepCount - step
                                //stepCount = todayCount
                                LocalData(this@StepDetectorService).storeTodayStepCount(todayCount)
                                Log.e("TAG", "stepCount same date: $stepCount", )
                                Log.e("TAG", "todayStep same date: $todayCount", )
                                Log.e("TAG", "step same date: $step", )

                            }
                        }
                    }
                }
                // Schedule the next run after 1 hour or more. – this delay will impact on battery
                handler.postDelayed(this, 3600000)
            }
        }

        handler.postDelayed(myRunnable, 0)
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        stepCount =  p0!!.values[0].roundToInt()
//        if (date != GeneralHelper.getToadyDate()) {
//            date = GeneralHelper.getToadyDate()
//            // stepCount=0
//            oldStepCount = p0!!.values[0].roundToInt()
//            Log.e("1010", "first step count $oldStepCount")
//            Log.e("1010", "today: date $date")
//            //sendStepCountAndDateBroadcast(todayStepCount.toString(), date)
//        } else {
//            todayStepCount = p0!!.values[0].roundToInt() - oldStepCount
//            //  Log.e("1010", ": same date $date", )
//            Log.e("1010", ": todayStepCount $todayStepCount")
//            Log.e("1010", ": ${p0!!.values[0].roundToInt()}")
//            sendStepCountAndDateBroadcast(todayStepCount.toString(), date)

        }

        /* scope.launch {
             LocalData(this@StepDetectorService).currentDate.collect {
                 if (it != GeneralHelper.getToadyDate()) {
                     LocalData(this@StepDetectorService).storeTotalStepCount(p0!!.values[0].roundToInt())
                     LocalData(this@StepDetectorService).storeCurrentDate(GeneralHelper.getToadyDate())
                 } else {
                     LocalData(this@StepDetectorService).totalStepCount.collect { step ->
                         val sensorSteps = p0!!.values[0].roundToInt()
                         val todayCount = sensorSteps - step
                         stepCount = todayCount
                         kotlinx.coroutines.delay(5000)
                         LocalData(this@StepDetectorService).storeTodayStepCount(todayCount)

                     }
                 }
             }

         }*/


    private fun sendStepCountAndDateBroadcast(stepCount: String, date: String) {
        val intent = Intent("com.app.driversafety.STEP_COUNT_UPDATE")
        intent.putExtra("stepCount", stepCount)
        intent.putExtra("date", date)
        sendBroadcast(intent)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d("SERVICE", p0.toString())
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        sensorManager.unregisterListener(this, countSensor)
    }
}
