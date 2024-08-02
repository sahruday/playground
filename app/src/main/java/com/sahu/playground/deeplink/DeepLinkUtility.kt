package com.sahu.playground.deeplink

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.sahu.playground.animations.CustomAnimation
import com.sahu.playground.rootDetection.RootDetection
import com.sahu.playground.docUpload.FilePickerActivity
import com.sahu.playground.location.LocationActivity

object DeepLinkUtility {

    @JvmStatic
    fun handleDeepLink(activity: Activity, url: Uri): Any? {
        if(url.authority != DeepLinkActivity.DEEPLINK_URI.authority) return null

        url.path?.takeIf { it.startsWith("/") }?.substring(1)?.let {
            when (val urlType = it.split("/")[0]) {
                FilePickerActivity.DEEPLINK_PATH -> {
                    val filePickerIntent = Intent(activity, FilePickerActivity::class.java)
                    activity.startActivity(filePickerIntent)
                }

                RootDetection.DEEPLINK_PATH -> {
                    val rootDetectionIntent = Intent(activity, RootDetection::class.java)
                    activity.startActivity(rootDetectionIntent)
                }

                LocationActivity.DEEPLINK_PATH -> {
                    val locationActivityIntent = Intent(activity, LocationActivity::class.java)
                    activity.startActivity(locationActivityIntent)
                }

                CustomAnimation.DEEPLINK_PATH -> {
                    val customAnimationIntent = Intent(activity, CustomAnimation::class.java)
                    activity.startActivity(customAnimationIntent)
                }

                else ->
                    Toast.makeText(activity, urlType, Toast.LENGTH_LONG).show()
            }
        }

        return null
    }
}