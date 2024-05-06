package com.app.yoursafetyfirst.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.response.Message
import com.app.yoursafetyfirst.response.PushNotificationResponse
import com.app.yoursafetyfirst.response.Title
import com.app.yoursafetyfirst.ui.notifications.NotificationDetailsActivity
import java.util.Random

class FirebaseMessageReceiver : FirebaseMessagingService() {

    // Override onNewToken to get new token
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("aaa", token)
    }

    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val response = Gson().fromJson(
            remoteMessage.data["notification"]?.get(1).toString(),
            PushNotificationResponse::class.java
        )
        remoteMessage.data.apply {
            sendNotification(
                Gson().fromJson(remoteMessage.data["title"], Title::class.java).en.toString(),
                Gson().fromJson(remoteMessage.data["message"],Message::class.java).en.toString(),
                remoteMessage.data["notificationId"].toString()
            )
        }

        Log.e("notification", "------------" + remoteMessage.notification.toString())
        Log.e("notification", "-------------" + remoteMessage.data.toString())


        /* remoteMessage.notification?.apply {
             sendNotification(
                 title.toString(),
                 body.toString(),
                 remoteMessage.data["notificationId"].toString()
             )
         }*/

    }

    private fun sendNotification(title: String, message: String, notificationId: String) {
        val random = Random()
        val generatedPassword = String.format("%06d", random.nextInt(10000))

        val intent = Intent(this, NotificationDetailsActivity::class.java)
        intent.putExtra("notificationId", notificationId)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(
            this, Integer.parseInt(generatedPassword),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val CHANNEL_ID = getString(R.string.app_name)
        var mChannel: NotificationChannel? = null
        mChannel = NotificationChannel(
            CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val nb = NotificationCompat.Builder(this, getString(R.string.app_name))
        nb.setSmallIcon(R.drawable.ic_launcher_round)
        nb.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
        nb.setContentTitle(title)
        nb.setContentText(message)
        nb.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        nb.setAutoCancel(true)
        nb.setSound(defaultSoundUri)
        nb.setLights(Color.GREEN, 3000, 3000)
        nb.setDefaults(Notification.DEFAULT_ALL)
        nb.priority = Notification.PRIORITY_HIGH
        nb.setContentIntent(pendingIntent)
        nb.setChannelId(CHANNEL_ID)
        val mNotificationManager =
            this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(mChannel)
        val random2 = Random()
        val generatedPassword2 = String.format("%06d", random2.nextInt(10000))
        mNotificationManager.notify(generatedPassword2.toInt(), nb.build())

    }


}