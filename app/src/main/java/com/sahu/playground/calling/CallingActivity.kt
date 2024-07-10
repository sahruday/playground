package com.sahu.playground.calling

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sahu.playground.appUtil.BaseActivity

class CallingActivity: BaseActivity() {
    @Composable
    override fun ComposableView() {
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Calling Activity")
            }
        }
    }
}