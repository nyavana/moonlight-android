package com.betterxcloud.client.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import com.betterxcloud.client.data.ControllerButton
import com.betterxcloud.client.data.ControllerKernel
import com.betterxcloud.client.data.VirtualKey
import com.betterxcloud.client.data.XCloudPreferences
import com.betterxcloud.client.data.XCloudPreferencesRepository
import kotlinx.coroutines.launch

/**
 * Navigation destinations available in the app.
 */
enum class Screen { Home, Options, Browser }

/**
 * Mutable state container shared across the composable screens. It exposes both the latest
 * preferences snapshot and lambdas that write back to the [XCloudPreferencesRepository].
 */
class BetterXCloudAppState(
    val repository: XCloudPreferencesRepository,
    val preferences: XCloudPreferences,
    val currentScreen: Screen,
    private val onScreenChanged: (Screen) -> Unit,
    private val coroutineScope: CoroutineScope,
) {
    fun navigateTo(screen: Screen) = onScreenChanged(screen)

    fun updateKernel(kernel: ControllerKernel) {
        if (preferences.controllerKernel == kernel) return
        launch { repository.updateKernel(kernel) }
    }

    fun updateMapping(button: ControllerButton, key: VirtualKey) {
        launch { repository.updateButton(button, key) }
    }

    fun resetMapping() {
        launch { repository.resetMapping() }
    }

    fun setKeepScreenAwake(enabled: Boolean) {
        if (preferences.keepScreenAwake == enabled) return
        launch { repository.setKeepScreenAwake(enabled) }
    }

    private fun launch(block: suspend () -> Unit) {
        coroutineScope.launch { block() }
    }
}

@Composable
fun rememberBetterXCloudAppState(repository: XCloudPreferencesRepository): BetterXCloudAppState {
    val preferences by repository.preferences.collectAsState(initial = XCloudPreferences.default())
    val screenState = remember { mutableStateOf(Screen.Home) }
    val coroutineScope = rememberCoroutineScope()
    return remember(preferences, screenState.value, coroutineScope) {
        BetterXCloudAppState(
            repository = repository,
            preferences = preferences,
            currentScreen = screenState.value,
            onScreenChanged = { destination -> screenState.value = destination },
            coroutineScope = coroutineScope,
        )
    }
}
