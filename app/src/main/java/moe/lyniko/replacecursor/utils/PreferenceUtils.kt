package moe.lyniko.replacecursor.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log

class ResourceHookEntry(resource_id: String, image_file: String, var enabled: Boolean,
                        var version: Int
) {
    var resourceId: String = resource_id
    var imageFile: String = image_file

    fun toPerfString(): String {
        return "$resourceId$delimiter$imageFile$delimiter$enabled$delimiter$version"
    }
    companion object {
        const val delimiter = ":"
        fun fromPerfString(perfString: String): ResourceHookEntry {
            val split = perfString.split(delimiter, limit = 4)
            return ResourceHookEntry(split[0], split[1], split[2].toBoolean(), split[3].toInt())
        }
    }
}

@SuppressLint("WorldReadableFiles")
class PreferenceUtils( // init context on constructor
    context: Context
) {
    // ------ 1. get several SharedPreferences (funcPref is the only accessible during Xposed inject) ------
    private var funcPref: SharedPreferences = try {
        @Suppress("DEPRECATION")
        context.getSharedPreferences(functionalConfigName, Context.MODE_WORLD_READABLE)
    } catch (e: SecurityException) {
        throw e
        // Log.w("PreferenceUtil", "Fallback to Private SharedPref for error!!!: ${e.message}")
        // context.getSharedPreferences(functionalConfigName, Context.MODE_PRIVATE)
    }

    var managerPref: SharedPreferences =
        context.getSharedPreferences(managerConfigName, Context.MODE_PRIVATE)
    var resourceHooks: List<ResourceHookEntry>

    companion object {
        @Volatile
        private var instance: PreferenceUtils? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: PreferenceUtils(context).also { instance = it }
            }

        const val functionalConfigName = "functional_config"
        const val managerConfigName = "manager_config"
        const val resourceHookConfigName = "resource_hook_config"
        const val imageBinaryPrefix = "image_binary_"

        enum class ConfigKeys(val key: String, val default: Boolean) {
            // HideNoActivityPackages("hide_no_activity_packages", true)
        }

        fun getResourceHooksFrom(preferences: SharedPreferences): List<ResourceHookEntry> {
            val resourceHooks =
                preferences.getStringSet(resourceHookConfigName, null) ?: return listOf()
            return resourceHooks.map { ResourceHookEntry.fromPerfString(it) }
        }

        fun getImageBinaryFrom(preferences: SharedPreferences, filename: String): ByteArray {
            val base64 = preferences.getString(imageBinaryPrefix + filename, null)!!
            return Base64.decode(base64, Base64.NO_WRAP)
        }
    }

    // ------ 2. get/set several SharedPreferences ------
    private fun initResourceHooks(): List<ResourceHookEntry> {
        return getResourceHooksFrom(funcPref)
    }

    init {
        resourceHooks = initResourceHooks()
    }

    private fun saveResourceHooks() {
        // Log.e("PreferenceUtils", "saveResourceHooks: $resourceHooks")
        val resourceHooksString = resourceHooks.map { it.toPerfString() }.toSet()
        funcPref.edit().putStringSet(resourceHookConfigName, resourceHooksString).apply()
    }

    fun addResourceHook(resourceHook: ResourceHookEntry) {
        // Log.e("PreferenceUtils", "addResourceHook: $resourceHook")
        resourceHooks = resourceHooks + resourceHook
        saveResourceHooks()
    }

    fun removeResourceHook(resourceHook: ResourceHookEntry) {
        // Log.e("PreferenceUtils", "removeResourceHook: $resourceHook")
        removeImageBinary(resourceHook.imageFile)
        resourceHooks = resourceHooks - resourceHook
        saveResourceHooks()
    }

    private fun getResourceHook(resourceId: String): ResourceHookEntry? {
        return resourceHooks.find { it.resourceId == resourceId }
    }

    fun updateResourceHook(resourceHook: ResourceHookEntry) {
        // Log.e("PreferenceUtils", "updateResourceHook: $resourceHook")
        val oldResourceHook = getResourceHook(resourceHook.resourceId)
        if (oldResourceHook == null) {
            addResourceHook(resourceHook)
        } else {
            removeResourceHook(oldResourceHook)
            addResourceHook(resourceHook)
        }
    }
    @SuppressLint("ApplySharedPref")
    fun setImageBinary(filename: String, data: ByteArray) {
        // Log.e("PreferenceUtils", "setImageBinary: $filename")
        funcPref.edit().putString(imageBinaryPrefix+filename, Base64.encodeToString(data, Base64.NO_WRAP)).apply()
    }
    fun getImageBinary(filename: String): ByteArray {
        // Log.e("PreferenceUtils", "getImageBinary: $filename")
        return getImageBinaryFrom(funcPref, filename)
    }
      private fun removeImageBinary(filename: String) {
        // Log.e("PreferenceUtils", "removeImageBinary: $filename")
        funcPref.edit().remove(imageBinaryPrefix+filename).apply()
    }
}