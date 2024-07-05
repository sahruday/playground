package com.sahu.playground.rootDetection

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sahu.playground.appUtil.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Keep in mind that no method is foolproof, and a determined attacker may still be able to bypass your detection mechanisms.
 */
@AndroidEntryPoint
class RootDetection : BaseActivity() {

    companion object {
        const val DEEPLINK_PATH = "rootDetection"
    }

    @Inject
    internal lateinit var appContext: Context

    @Composable
    override fun ComposableView() {
        val rootCheckMap = remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
        LaunchedEffect(key1 = true) {
            delay(2000L)
            isDeviceRooted(rootCheckMap)
        }
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
                .padding(16.dp + 5.dp)
        ) {
            Column {
                Text(
                    text = "✔ : Safe             ❌ : Unsafe",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                    style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
                )
                rootCheckMap.value.takeIf { it.isNotEmpty() }?.let {
                    Text(
                        text = it.map { "${it.key} -> ${if (!it.value) "✔" else "❌"}" }
                            .joinToString("\n"),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                } ?: run {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .size(50.dp)
                    )
                }
            }
        }
    }

    private suspend fun isDeviceRooted(rootCheckMap: MutableState<Map<String, Boolean>>) =
        withContext(Dispatchers.IO) {
            rootCheckMap.value = mapOf("Is Emulator Check" to checkIsEmulator())
            delay(200L)
            checkBinary(rootCheckMap)
            checkRootFilesAndPackages(rootCheckMap)
        }

    private suspend fun checkBinary(rootCheckMap: MutableState<Map<String, Boolean>>) {
        val paths = arrayOf(
            "/system/etc/security/otacerts.zip", //Passes for signed apk only
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/system/xbin/mu"
        )
        for (path in paths) {
            val map = rootCheckMap.value.toMutableMap()
            map[path] = File(path).exists()
            rootCheckMap.value = map
            delay(200L)
        }
    }


    private fun checkIsEmulator(): Boolean {

        return ((Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                && Build.FINGERPRINT.endsWith(":user/release-keys")
                && Build.MANUFACTURER == "Google" && Build.PRODUCT.startsWith("sdk_gphone") && Build.BRAND == "google"
                && Build.MODEL.startsWith("sdk_gphone"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HOST == "Build2" //MSI App Player
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT == "google_sdk"
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator"))
    }

    private suspend fun checkRootFilesAndPackages(rootCheckMap: MutableState<Map<String, Boolean>>) {

        val knownRootAppsPackages = listOf(
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk",
            "com.kingroot.kinguser",
            "com.kingo.root",
            "com.smedialink.oneclickroot",
            "com.zhiqupk.root.global",
            "com.alephzain.framaroot"
        )
        for (packageName in knownRootAppsPackages) {
            val map = rootCheckMap.value.toMutableMap()
            map[packageName] = isPackageInstalled(packageName)
            rootCheckMap.value = map
            delay(200L)
        }
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        val pm = appContext.packageManager
        return try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}