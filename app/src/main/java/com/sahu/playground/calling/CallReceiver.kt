package com.sahu.playground.calling

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class CallReceiver: BroadcastReceiver() {
    companion object{
        const val TAG = "Call Receiver"
    }
    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(TAG, "action received $intent")
        when (intent?.action) {
            "ANSWER_CALL" -> {
                Log.i(TAG, "Answered")
                Toast.makeText(context, "Call Answered", Toast.LENGTH_LONG).show()
                // Handle the call answer logic here
            }
            "REJECT_CALL" -> {
                Log.i(TAG, "Declined")
                Toast.makeText(context, "Call Rejected", Toast.LENGTH_LONG).show()
                // Handle the call reject logic here
            }
        }

       context.stopOngoingCallService()
    }
}