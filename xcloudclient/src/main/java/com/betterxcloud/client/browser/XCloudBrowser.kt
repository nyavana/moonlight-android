package com.betterxcloud.client.browser

import android.content.res.AssetManager
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.betterxcloud.client.data.XCloudPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

private const val XCLOUD_URL = "https://www.xbox.com/en-US/play"

/**
 * Single-tab Chromium wrapper responsible for bootstrapping the Better XCloud user script and
 * applying controller configuration before the player is rendered.
 */
@Composable
fun XCloudBrowser(
    preferences: XCloudPreferences,
) {
    val context = LocalContext.current
    val assets = context.assets
    var scriptContent by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(assets) {
        scriptContent = loadScript(assets)
    }

    val latestPreferences by rememberUpdatedState(preferences)
    val latestScript by rememberUpdatedState(scriptContent)

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                configureWebSettings(settings)
                webChromeClient = FullscreenChromeClient()
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        val payload = buildScriptPayload(latestPreferences, latestScript)
                        if (payload != null) {
                            view.evaluateJavascript(payload, null)
                        }
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest,
                    ): Boolean {
                        // Block navigation away from the XCloud ecosystem to keep the app focused
                        // on a single-tab experience while still allowing Microsoft account auth.
                        val host = request.url.host?.lowercase() ?: return false
                        val allowedHosts = listOf("xbox.com", "microsoft.com", "live.com", "msauth.net", "msftauth.net")
                        return allowedHosts.none { host.endsWith(it) }
                    }
                }
                setDownloadListener { _, _, _, _, _ -> }
                setOnLongClickListener { true }
                isHapticFeedbackEnabled = false
                loadUrl(XCLOUD_URL)
            }
        }, update = { webView ->
            val payload = buildScriptPayload(latestPreferences, latestScript)
            if (payload != null) {
                webView.evaluateJavascript(payload, null)
            }
        })
    }
}

private fun configureWebSettings(settings: WebSettings) {
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.databaseEnabled = true
    settings.mediaPlaybackRequiresUserGesture = false
    settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
    settings.cacheMode = WebSettings.LOAD_DEFAULT
    settings.useWideViewPort = true
    settings.loadWithOverviewMode = true
    settings.userAgentString = settings.userAgentString + " BetterXcloudClient/1.0"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        settings.safeBrowsingEnabled = true
    }
}

private fun buildScriptPayload(
    preferences: XCloudPreferences?,
    script: String?,
): String? {
    if (preferences == null || script.isNullOrEmpty()) return null
    val config = JSONObject().apply {
        put("kernel", preferences.controllerKernel.id)
        put("keepAwake", preferences.keepScreenAwake)
        put("mapping", JSONObject().apply {
            preferences.buttonMapping.forEach { (button, key) ->
                put(button.scriptKey, key.code)
            }
        })
    }

    val bootstrap = "window.__betterXcloudConfig = ${config};"
    return "(function(){${bootstrap}\n${script}\n})();"
}

private suspend fun loadScript(assets: AssetManager): String = withContext(Dispatchers.IO) {
    assets.open("better_xcloud.user.js").use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
    }
}

private class FullscreenChromeClient : WebChromeClient()
