package com.app.yoursafetyfirst

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
//import com.app.driversafety.healthconnect.HealthConnectManager
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class YourSafetyFirstApp : Application() {

    companion object {
        var selectedLanguage: String = ""
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
       /* val healthConnectManager by lazy {
          //  HealthConnectManager(this)
        }*/
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            "CHANNEL_ID",
            "Driver Safety",
            NotificationManager.IMPORTANCE_LOW
        )
        serviceChannel.vibrationPattern = LongArray(0) { 0 }
        serviceChannel.setSound(null, null)
        serviceChannel.enableVibration(true)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

}