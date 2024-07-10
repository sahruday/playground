package com.sahu.playground.calling

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class CallReceiver: BroadcastReceiver() {
    companion object{
        const val TAG = "Call Receiver"

        const val ANSWER_CALL = "ANSWER_CALL"
        const val REJECT_CALL = "REJECT_CALL"
    }
    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(TAG, "action received $intent")
        when (intent?.action) {
            ANSWER_CALL -> {
                Log.i(TAG, "Answered")
                Toast.makeText(context, "Call Answered", Toast.LENGTH_LONG).show()
                openCallingActivity(context, ANSWER_CALL)
            }
            REJECT_CALL -> {
                Log.i(TAG, "Declined")
                Toast.makeText(context, "Call Rejected", Toast.LENGTH_LONG).show()
                // Handle the call reject logic here
            }
        }

       context.stopOngoingCallService()
    }

    private fun openCallingActivity(context: Context, action: String? = null) {
        val callActivityIntent = Intent(context, CallingActivity::class.java).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setAction(Intent.ACTION_MAIN)
            setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            action?.let { this.action = action }
        }
        context.startActivity(callActivityIntent)
    }
}