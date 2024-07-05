@file:OptIn(ExperimentalMaterial3Api::class)

package com.sahu.playground

import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sahu.playground.appUtil.BaseActivity
import com.sahu.playground.deeplink.DeepLinkActivity
import com.sahu.playground.deeplink.DeepLinkUtility
import com.sahu.playground.docUpload.FilePickerActivity
import com.sahu.playground.location.LocationActivity
import com.sahu.playground.rootDetection.RootDetection
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private val splashViewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setUpSplashScreen()
        super.onCreate(savedInstanceState)
    }

    @Composable
    override fun ComposableView() {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(title = { Text(text = "Sahu's Play ground") })
            },
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(top = 50.dp)
                    .padding(horizontal = 16.dp)
            ) {
                items(options) {
                    OutlinedCard(
                        onClick = {
                            it.deeplinkUrl.takeIf { it.isNotBlank() }?.let {
                                DeepLinkUtility.handleDeepLink(this@MainActivity, Uri.parse(it))
                            }
                        },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 16.dp, vertical = 24.dp)
                            ) {
                                Text(text = it.title)
                                it.description.takeIf { it.isNotBlank() }?.let {
                                    Text(text = it, modifier = Modifier.padding(top = 8.dp))
                                }
                            }

                            Icon(
                                painter = rememberVectorPainter(image = Icons.Filled.KeyboardArrowRight),
                                contentDescription = "",
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setUpSplashScreen() {
        installSplashScreen().setKeepOnScreenCondition {
            theme
            splashViewModel.isSplashShow.value
        }
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.height.toFloat()
            )
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 200L

            slideUp.doOnEnd {
                splashScreenView.remove()
            }
            slideUp.start()
        }
    }

    data class Option(
        val title: String,
        val deeplinkUrl: String,
        val description: String = "",
    )

    companion object {
        private val RootDetector = Option("Root detection", DeepLinkActivity.DEEPLINK_PREFIX + RootDetection.DEEPLINK_PATH)
        private val Location = Option("Location", DeepLinkActivity.DEEPLINK_PREFIX + LocationActivity.DEEPLINK_PATH)
        private val FileChooser = Option(
            "File chooser",
            DeepLinkActivity.DEEPLINK_PREFIX + FilePickerActivity.DEEPLINK_PATH
        )
        private val Testing = Option("Test toast", DeepLinkActivity.DEEPLINK_PREFIX + "testing")

        val options = listOf(RootDetector, Location, FileChooser, Testing)
    }
}

