package com.app.yoursafetyfirst.utils.stepcount

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import androidx.core.app.NotificationCompat
import com.app.yoursafetyfirst.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class GeneralHelper {

    companion object {
        fun getToadyDate(): String {
            val date: Date = Calendar.getInstance().time
            val df: DateFormat = SimpleDateFormat("dd MMM yyyy")
            return df.format(date)
        }

        fun updateNotification(context: Context, service: Service) {
            val NOTIFICATION_ID = 7837
            val notiManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(context, "CHANNEL_ID")
                .setContentTitle("Driver Safety")
                //.setContentText(step.toString())
                //.setTicker(step.toString())
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                //.setStyle(NotificationCompat.BigTextStyle().bigText("Driver Safety"))
                //.setStyle(NotificationCompat.BigTextStyle().bigText(step.toString()))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                //.setContentIntent(pendingIntent)
                //.setProgress(50000, step, false)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .build()


            service.startForeground(NOTIFICATION_ID, notification)
            // Set Service to run in the Foreground
            notiManager.notify(NOTIFICATION_ID, notification)

        }


    }
}