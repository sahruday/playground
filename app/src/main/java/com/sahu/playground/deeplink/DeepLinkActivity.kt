package com.sahu.playground.deeplink

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier

class DeepLinkActivity: ComponentActivity() {

    companion object{
        const val DEEPLINK_PREFIX = "https://playground.sahu.com/"
        val DEEPLINK_URI = Uri.parse(DEEPLINK_PREFIX)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val data = intent?.data?.also {
            DeepLinkUtility.handleDeepLink(this, it)
        }
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(text = data?.path ?: "")
            }
        }
//        finish()
    }
}