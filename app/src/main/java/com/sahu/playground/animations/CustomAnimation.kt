package com.sahu.playground.animations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.sahu.playground.appUtil.BaseActivity

class CustomAnimation : BaseActivity() {

    companion object {
        const val DEEPLINK_PATH = "customAnimation"
    }

    @Composable
    override fun ComposableView() {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding(),
            color = Color.White
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProgressTextButton(
                    text = AnnotatedString("Next Episode"),
                    duration = 5000,
                    contentPaddingValues = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
                ) {  }
            }
        }
    }
}
