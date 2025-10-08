package com.betterxcloud.client

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.betterxcloud.client.data.XCloudPreferencesRepository
import com.betterxcloud.client.ui.BetterXCloudApp
import com.betterxcloud.client.ui.rememberBetterXCloudAppState

/**
 * Main entry point for the Better XCloud client. The activity uses Jetpack Compose for the UI,
 * keeping the code compact and highly testable.
 */
class MainActivity : ComponentActivity() {

    private lateinit var repository: XCloudPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = XCloudPreferencesRepository(this)
        enableImmersiveMode()

        setContent {
            BetterXCloudTheme {
                val appState = rememberBetterXCloudAppState(repository)
                KeepScreenAwakeEffect(appState.preferences.keepScreenAwake)

                Surface(color = MaterialTheme.colorScheme.background) {
                    BetterXCloudApp(appState)
                }
            }
        }
    }

    private fun enableImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }
}

@Composable
private fun KeepScreenAwakeEffect(keepScreenOn: Boolean) {
    val context = LocalContext.current
    val window = remember(context) { (context as? ComponentActivity)?.window }
    DisposableEffect(window, keepScreenOn) {
        if (keepScreenOn) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}

@Composable
fun BetterXCloudTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = if (darkTheme) BetterXCloudColors.dark else BetterXCloudColors.light
    MaterialTheme(colorScheme = colorScheme, typography = MaterialTheme.typography, content = content)
}

private object BetterXCloudColors {
    val light = androidx.compose.material3.lightColorScheme(
        primary = Color(0xFF0F6CBD),
        onPrimary = Color.White,
        secondary = Color(0xFF107C10),
        onSecondary = Color.White,
        background = Color(0xFFF4F5F7),
        surface = Color.White,
    )

    val dark = androidx.compose.material3.darkColorScheme(
        primary = Color(0xFF88BFFF),
        onPrimary = Color(0xFF002D5B),
        secondary = Color(0xFF57D757),
        onSecondary = Color(0xFF003000),
        background = Color(0xFF101214),
        surface = Color(0xFF1E1F22),
    )
}
