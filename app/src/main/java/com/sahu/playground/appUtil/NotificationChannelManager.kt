package com.sahu.playground.appUtil

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager

object NotificationChannelManager {

    const val CALLING = "In-app Calling"
    const val MISCELLANEOUS = "Miscellaneous"

    fun createNotificationChannels(context: Context) {
        callNotificationChannel(context)
        generalNotificationChannel(context)
    }

    fun callNotificationChannel(context: Context) {
        val notificationChannel = NotificationChannel(
            CALLING,
            "Calling",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "This is used for calling notification"
            enableVibration(true)
            setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            vibrationPattern = longArrayOf(0, 1000, 500, 1000)
        }
        val notificationManager = context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager

        // Setting up the channel
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun generalNotificationChannel(context: Context) {
        val notificationChannel = NotificationChannel(
            MISCELLANEOUS,
            "Miscellaneous",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager

        // Setting up the channel
        notificationManager.createNotificationChannel(notificationChannel)
    }
 }