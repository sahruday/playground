package com.sahu.playground.calling

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.IBinder
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import com.sahu.playground.R
import com.sahu.playground.appUtil.NotificationChannelManager
import com.sahu.playground.appUtil.PhoneController
import com.sahu.playground.calling.CallReceiver.Companion.ANSWER_CALL
import com.sahu.playground.calling.CallReceiver.Companion.REJECT_CALL


class CallService: Service() {
    companion object{
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val RINGING_DURATION = 30L * 1000L
    }

    private var ringtone: Ringtone? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        NotificationChannelManager.callNotificationChannel(this)
    }

    private fun createIncomingCallNotification(callerName: String, callerNumber: String) : Notification{
        val answerIntent = Intent(this, CallReceiver::class.java).apply {
            action = ANSWER_CALL
        }
        val answerPendingIntent = PendingIntent.getBroadcast(this, 0, answerIntent, PendingIntent.FLAG_IMMUTABLE)

        val rejectIntent = Intent(this, CallReceiver::class.java).apply {
            action = REJECT_CALL
        }
        val rejectPendingIntent = PendingIntent.getBroadcast(this, 0, rejectIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationIntent = Intent(applicationContext, CallingActivity::class.java)
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        notificationIntent.setAction(Intent.ACTION_MAIN)
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val resultIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val person = Person.Builder()
            .setName(callerName)
            .build()

        return NotificationCompat.Builder(this, NotificationChannelManager.CALLING)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Incoming call")
            .setContentText("In-coming call from $callerNumber")
            .setStyle(
                NotificationCompat.CallStyle.forIncomingCall(
                    person,
                    rejectPendingIntent,
                    answerPendingIntent,
                )
            )
            .setOngoing(true)
            .setTimeoutAfter(RINGING_DURATION)
            .setContentIntent(resultIntent)
            .build()
    }

    private fun startVibrate() {
        val v: Vibrator = PhoneController.getVibrator(applicationContext)
        PhoneController.startVibration(v)
    }

    private fun stopVibrate() {
        val v: Vibrator = PhoneController.getVibrator(applicationContext)
        PhoneController.stopVibration(v)
    }

    private fun startRingtone() {
        ringtone = RingtoneManager.getRingtone(applicationContext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
        ringtone?.isLooping = true
        if(ringtone?.isPlaying == true)
            stopRingtone()
        ringtone?.play()

    }

    private fun stopRingtone() {
        ringtone?.stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_SERVICE -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
//                stopRingtone()
//                stopVibrate()
            }
            else -> {
                val callerName = intent?.getStringExtra("caller_name") ?: "Unknown Caller"
                val callerNumber = intent?.getStringExtra("caller_number") ?: "Unknown Number"
                startForeground(1, createIncomingCallNotification(callerName, callerNumber))
//                startRingtone()
//                startVibrate()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
//        stopRingtone()
//        stopVibrate()
    }

}