package com.sahu.playground.appUtil

import android.app.Activity
import android.net.Uri
import com.sahu.playground.deeplink.DeepLinkUtility

sealed interface Action {
    fun performAction(activity: Activity)
}
data class DeeplinkAction(val deeplinkUrl: String): Action {
    override fun performAction(activity: Activity) {
        deeplinkUrl.takeIf { it.isNotBlank() }?.let {
            DeepLinkUtility.handleDeepLink(activity, Uri.parse(it))
        }
    }
}
data class CustomAction(val onClick: (activity: Activity) -> Unit): Action {
    override fun performAction(activity: Activity) {
        onClick.invoke(activity)
    }
}