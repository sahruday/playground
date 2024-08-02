package com.sahu.playground

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.sahu.playground.appUtil.NotificationChannelManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Playground: Application() {

    companion object{
        const val TAG = "Playground"
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        notificationChannel()
//        getMessageKey()
    }

    private fun notificationChannel(){
        NotificationChannelManager.createNotificationChannels(this)
    }

    private fun getMessageKey() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = "Firebase Message token: $token"
            Log.d(TAG, msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }


}