package com.sahu.playground.fcmService

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sahu.playground.MainActivity
import com.sahu.playground.R
import com.sahu.playground.appUtil.NotificationChannelManager
import com.sahu.playground.calling.CallService
import com.sahu.playground.calling.CallingActivity


class FirebaseCloudMessageService: FirebaseMessagingService() {

    companion object{
        const val TAG = "FCM Service"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("FCM Token", "Token refreshed, new token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val extras = Bundle()

        val dataPayLoad = remoteMessage.data
        for ((key, value) in dataPayLoad) {
            Log.i(TAG,"dataPayLoad: $key -> $value")
            extras.putString(key, value)
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: " + it.body)
            extras.putString("notificationPayload",it.body)
        }

        if(dataPayLoad.getOrDefault("type", "Calling") == "Calling") {
            showCallNotification(dataPayLoad, extras)
        } else {
            showNotification(dataPayLoad, extras)
        }
    }

    private fun showCallNotification(dataPayLoad: Map<String, String>, extras: Bundle) {
        // Check if message contains a notification payload.
        val name = dataPayLoad.getOrDefault("name", "Unknown")
        val number = dataPayLoad.getOrDefault("number", "Unknown")

        val serviceIntent = Intent(this, CallService::class.java).apply {
            putExtra("caller_name", name)
            putExtra("caller_number", number)
        }
        try {
            startForegroundService(serviceIntent)
        }catch (e: Exception) {
            Log.i(TAG, "App is in the background, starting activity")
            val callActivityIntent = Intent(this, CallingActivity::class.java).apply {
                putExtra("caller_name", name)
                putExtra("caller_number", number)
                addCategory(Intent.CATEGORY_LAUNCHER)
                setAction(Intent.ACTION_MAIN)
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(callActivityIntent)
        }
    }


    private fun showNotification(
        dataPayLoad: Map<String, String>,
        extras: Bundle
    ) {
        val channelID = NotificationChannelManager.MISCELLANEOUS

        val title = if (dataPayLoad.containsKey("title")) dataPayLoad["title"] else "title"
        val body = if (dataPayLoad.containsKey("body")) dataPayLoad["body"] else "body"


        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        notificationIntent.setAction(Intent.ACTION_MAIN)
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        notificationIntent.putExtras(extras)

        val resultIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }


        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultIntent)
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(0, mBuilder.build())
    }
}