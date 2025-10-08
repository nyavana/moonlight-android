package com.betterxcloud.client

import android.app.Application
import android.webkit.WebView

/**
 * Application entry point used to perform process-wide configuration.
 *
 * We enable WebView debugging in debug builds to make it easier to iterate on
 * the embedded Chromium instance and on the Better XCloud user script.
 */
class XCloudApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
    }
}
