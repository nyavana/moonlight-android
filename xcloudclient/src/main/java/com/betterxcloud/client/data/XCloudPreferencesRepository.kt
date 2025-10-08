package com.betterxcloud.client.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.io.IOException

private const val DATA_STORE_NAME = "better_xcloud_preferences"

private val Context.dataStore by preferencesDataStore(name = DATA_STORE_NAME)

/**
 * Persistence layer responsible for storing the user's controller kernel selection, key mappings,
 * and other quality-of-life toggles. The information is saved in a DataStore which gives us a
 * structured asynchronous API with automatic migration off the main thread.
 */
class XCloudPreferencesRepository(private val context: Context) {

    private object Keys {
        val controllerKernel = stringPreferencesKey("controller_kernel")
        val controllerMapping = stringPreferencesKey("controller_mapping")
        val keepScreenAwake = booleanPreferencesKey("keep_screen_awake")
    }

    /** A hot [Flow] that emits the latest preference snapshot whenever it changes. */
    val preferences: Flow<XCloudPreferences> = context.dataStore.data
        .catch { throwable ->
            if (throwable is IOException) emit(emptyPreferences())
            else throw throwable
        }
        .map { prefs ->
            val default = XCloudPreferences.default()
            val kernel = ControllerKernel.fromId(prefs[Keys.controllerKernel])
            val mappingJson = prefs[Keys.controllerMapping]
            val mapping = if (mappingJson.isNullOrEmpty()) {
                default.buttonMapping
            } else {
                parseMapping(mappingJson, default)
            }
            val keepAwake = prefs[Keys.keepScreenAwake] ?: default.keepScreenAwake
            default.copy(controllerKernel = kernel, buttonMapping = mapping, keepScreenAwake = keepAwake)
        }

    suspend fun updateKernel(kernel: ControllerKernel) {
        context.dataStore.edit { prefs ->
            prefs[Keys.controllerKernel] = kernel.id
        }
    }

    suspend fun updateButton(button: ControllerButton, key: VirtualKey) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.controllerMapping]
            val mapping = if (current.isNullOrEmpty()) {
                JSONObject()
            } else {
                JSONObject(current)
            }
            mapping.put(button.scriptKey, key.code)
            prefs[Keys.controllerMapping] = mapping.toString()
        }
    }

    suspend fun resetMapping() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.controllerMapping)
        }
    }

    suspend fun setKeepScreenAwake(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.keepScreenAwake] = enabled
        }
    }

    private fun parseMapping(serialized: String, default: XCloudPreferences): Map<ControllerButton, VirtualKey> {
        val defaults = default.buttonMapping
        val json = JSONObject(serialized)
        return ControllerButton.orderedButtons.associateWith { button ->
            val stored = json.optString(button.scriptKey, null)
            if (stored.isNullOrEmpty()) defaults[button] ?: VirtualKey.fromCode(null)
            else VirtualKey.fromCode(stored)
        }
    }
}
