package com.sahu.playground.fcmService

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.Ringtone
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sahu.playground.R
import com.sahu.playground.appUtil.NotificationChannelManager
import com.sahu.playground.calling.CallReceiver
import com.sahu.playground.calling.CallService
import com.sahu.playground.calling.CallingActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseCloudMessageService: FirebaseMessagingService() {

    companion object{
        const val TAG = "FCM Service"
    }

    @Inject
    lateinit var ringtone: Ringtone

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
//            showNotification(dataPayLoad, extras)
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
            showNotification(dataPayLoad, extras)
        }
    }

    private fun showNotification(
        dataPayLoad: Map<String, String>,
        extras: Bundle
    ) {
        val title = if (dataPayLoad.containsKey("name")) dataPayLoad["name"] else "Unknown Caller"
        val body = if (dataPayLoad.containsKey("number")) dataPayLoad["number"] else "Unknow Number"


        val notificationIntent = Intent(applicationContext, CallingActivity::class.java)
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        notificationIntent.setAction(Intent.ACTION_MAIN)
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        notificationIntent.putExtras(extras)

        val resultIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val answerIntent = Intent(this, CallReceiver::class.java).apply {
            action = CallReceiver.ANSWER_CALL
        }
        val answerPendingIntent = PendingIntent.getBroadcast(this, 0, answerIntent, PendingIntent.FLAG_IMMUTABLE)

        val rejectIntent = Intent(this, CallReceiver::class.java).apply {
            action = CallReceiver.REJECT_CALL
        }
        val rejectPendingIntent = PendingIntent.getBroadcast(this, 0, rejectIntent, PendingIntent.FLAG_IMMUTABLE)

        val person = Person.Builder()
            .setName(title)
            .build()

        val mBuilder = NotificationCompat.Builder(this, NotificationChannelManager.CALLING)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Incoming call")
            .setContentText("In-coming call from $body")
            .setStyle(
                NotificationCompat.CallStyle.forIncomingCall(
                    person,
                    rejectPendingIntent,
                    answerPendingIntent,
                )
            )
            .setTimeoutAfter(CallService.RINGING_DURATION)
            .setFullScreenIntent(resultIntent, true)

        val mNotificationManager = getSystemService(NotificationManager::class.java)
        mNotificationManager.notify(2, mBuilder.build())
    }
}