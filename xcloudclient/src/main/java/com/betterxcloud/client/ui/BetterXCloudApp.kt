package com.betterxcloud.client.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.betterxcloud.client.R
import com.betterxcloud.client.browser.XCloudBrowser
import com.betterxcloud.client.data.ControllerButton
import com.betterxcloud.client.data.ControllerKernel
import com.betterxcloud.client.data.VirtualKey

/**
 * Root composable that wires together the navigation destinations declared in [Screen].
 */
@Composable
fun BetterXCloudApp(appState: BetterXCloudAppState) {
    when (appState.currentScreen) {
        Screen.Home -> HomeScreen(
            onStart = { appState.navigateTo(Screen.Browser) },
            onOptions = { appState.navigateTo(Screen.Options) },
            preferencesSummary = stringResource(
                id = R.string.home_selected_kernel,
                stringResource(appState.preferences.controllerKernel.label)
            )
        )

        Screen.Options -> OptionsScreen(appState)
        Screen.Browser -> BrowserScreen(appState)
    }
}

@Composable
private fun HomeScreen(onStart: () -> Unit, onOptions: () -> Unit, preferencesSummary: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Gamepad,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(96.dp)
            )
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.home_description),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = preferencesSummary,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.home_start_button))
            }
            OutlinedButton(onClick = onOptions, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.home_options_button))
            }
        }
    }
}

@Composable
private fun OptionsScreen(appState: BetterXCloudAppState) {
    val buttons = remember { ControllerButton.orderedButtons }
    val mappingState = remember { mutableStateOf<ControllerButton?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.options_title)) },
                navigationIcon = {
                    IconButton(onClick = { appState.navigateTo(Screen.Home) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.content_back))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                KernelCard(
                    selected = appState.preferences.controllerKernel,
                    onSelect = { kernel -> appState.updateKernel(kernel) }
                )
            }
            item {
                KeepAwakeCard(
                    enabled = appState.preferences.keepScreenAwake,
                    onToggle = { appState.setKeepScreenAwake(it) }
                )
            }
            item {
                MappingHeader(onReset = { appState.resetMapping() })
            }
            items(buttons) { button ->
                MappingRow(
                    button = button,
                    selectedKey = appState.preferences.buttonMapping[button] ?: VirtualKey.KeyK,
                    onClick = { mappingState.value = button }
                )
            }
        }
    }

    val selectedButton = mappingState.value
    if (selectedButton != null) {
        MappingDialog(
            button = selectedButton,
            current = appState.preferences.buttonMapping[selectedButton] ?: VirtualKey.KeyK,
            onDismiss = { mappingState.value = null },
            onConfirm = { key ->
                appState.updateMapping(selectedButton, key)
                mappingState.value = null
            }
        )
    }
}

@Composable
private fun BrowserScreen(appState: BetterXCloudAppState) {
    BackHandler { appState.navigateTo(Screen.Home) }
    XCloudBrowser(preferences = appState.preferences)
}

@Composable
private fun KernelCard(selected: ControllerKernel, onSelect: (ControllerKernel) -> Unit) {
    ElevatedCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = stringResource(R.string.kernel_title), style = MaterialTheme.typography.titleMedium)
            Text(text = stringResource(R.string.kernel_subtitle), style = MaterialTheme.typography.bodyMedium)
            ControllerKernel.entries.forEach { kernel ->
                FilledIconToggleButton(
                    checked = kernel == selected,
                    onCheckedChange = { if (it) onSelect(kernel) }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = stringResource(kernel.label), fontWeight = FontWeight.Medium)
                        Text(text = stringResource(kernel.description), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun KeepAwakeCard(enabled: Boolean, onToggle: (Boolean) -> Unit) {
    ElevatedCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = stringResource(R.string.keep_awake_title), style = MaterialTheme.typography.titleMedium)
            Text(text = stringResource(R.string.keep_awake_body), style = MaterialTheme.typography.bodyMedium)
            FilledIconToggleButton(checked = enabled, onCheckedChange = onToggle) {
                val label = if (enabled) R.string.keep_awake_on else R.string.keep_awake_off
                Text(text = stringResource(label))
            }
        }
    }
}

@Composable
private fun MappingHeader(onReset: () -> Unit) {
    RowCard {
        Text(
            text = stringResource(R.string.mapping_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onReset) {
            Text(text = stringResource(R.string.mapping_reset))
        }
    }
}

@Composable
private fun MappingRow(button: ControllerButton, selectedKey: VirtualKey, onClick: () -> Unit) {
    RowCard(onClick = onClick) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = stringResource(button.displayName), style = MaterialTheme.typography.titleMedium)
            Text(text = stringResource(R.string.mapping_current, stringResource(selectedKey.friendlyName)))
        }
        Icon(imageVector = Icons.Default.Tune, contentDescription = null)
    }
}

@Composable
private fun RowCard(onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    val modifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    } else {
        Modifier.fillMaxWidth()
    }

    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun MappingDialog(
    button: ControllerButton,
    current: VirtualKey,
    onDismiss: () -> Unit,
    onConfirm: (VirtualKey) -> Unit,
) {
    val selected = remember { mutableStateOf(current) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.mapping_dialog_title, stringResource(button.displayName))) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(VirtualKey.entries) { key ->
                    DropdownMenuItem(
                        text = { Text(stringResource(key.friendlyName)) },
                        onClick = { selected.value = key },
                        trailingIcon = if (key == selected.value) {
                            { Icon(Icons.Default.Settings, contentDescription = null) }
                        } else null
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selected.value) }) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
