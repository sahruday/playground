package com.sahu.playground.appUtil

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator

object PhoneController {
    fun getVibrator(context: Context) = context.getSystemService(Vibrator::class.java)

    fun startVibration(vibrator: Vibrator) =
        vibrator.vibrate(
            VibrationEffect.createWaveform(longArrayOf(0, 100L, 500L, 100L), 3)
        )

    fun stopVibration(vibrator: Vibrator) = vibrator.cancel()

}