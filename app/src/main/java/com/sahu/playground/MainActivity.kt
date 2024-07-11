@file:OptIn(ExperimentalMaterial3Api::class)

package com.sahu.playground

import android.Manifest.permission.POST_NOTIFICATIONS
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.sahu.playground.appUtil.Action
import com.sahu.playground.appUtil.BaseActivity
import com.sahu.playground.appUtil.CustomAction
import com.sahu.playground.appUtil.DeeplinkAction
import com.sahu.playground.deeplink.DeepLinkActivity
import com.sahu.playground.docUpload.FilePickerActivity
import com.sahu.playground.location.LocationActivity
import com.sahu.playground.rootDetection.RootDetection
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private val splashViewModel by viewModels<SplashViewModel>()

    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setUpSplashScreen { requestPermissionsIfNotPresent() }
        super.onCreate(savedInstanceState)
    }

    private fun requestPermissionsIfNotPresent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(POST_NOTIFICATIONS))
        }
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
                        onClick = { it.action.performAction(this@MainActivity) },
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

    private fun setUpSplashScreen(onSplashScreenExited: () -> Unit) {
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
                onSplashScreenExited.invoke()
            }
            slideUp.start()
        }
    }

    data class Option(
        val title: String,
        val action: Action,
        val description: String = "",
    )

    companion object {
        private val RootDetector = Option("Root detection", DeeplinkAction(DeepLinkActivity.DEEPLINK_PREFIX + RootDetection.DEEPLINK_PATH))
        private val Location = Option("Location", DeeplinkAction(DeepLinkActivity.DEEPLINK_PREFIX + LocationActivity.DEEPLINK_PATH))
        private val FileChooser = Option("File chooser", DeeplinkAction(DeepLinkActivity.DEEPLINK_PREFIX + FilePickerActivity.DEEPLINK_PATH))
        private val Testing = Option("Test toast", DeeplinkAction(DeepLinkActivity.DEEPLINK_PREFIX + "testing"))
        private val copyFirebaseToken = Option("Copy token to clipboard", CustomAction {
            copyFirebaseTokenToClipboard(it)
        })

        val options = listOf(RootDetector, Location, FileChooser, Testing, copyFirebaseToken)

        fun copyFirebaseTokenToClipboard(activity: Activity) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(Playground.TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result

                Log.d("Firebase Message token", token)
                val clipboardManager = activity.getSystemService(ClipboardManager::class.java)
                clipboardManager.setPrimaryClip(ClipData.newPlainText("FCM token", token))
                Toast.makeText(activity, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            })
        }
    }
}

