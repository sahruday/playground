package com.sahu.playground.appUtil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.sahu.playground.ui.theme.PlaygroundTheme

abstract class BaseActivity: ComponentActivity() {

    open override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            PlaygroundTheme {
                ComposableView()
            }
        }
    }

    @Composable
    abstract fun ComposableView()
}