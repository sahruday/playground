package com.sahu.playground.calling

import android.content.Context
import android.content.Intent

fun Context.stopOngoingCallService() {
    val stopIntent = Intent(this, CallService::class.java)
    stopIntent.action = CallService.ACTION_STOP_SERVICE
    this.startService(stopIntent)
}