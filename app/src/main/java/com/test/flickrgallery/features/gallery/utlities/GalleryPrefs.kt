package com.test.flickrgallery.features.gallery.utlities

import android.content.Context
import androidx.core.content.edit

class GalleryPrefs(context: Context) {

    private val sharedPref = context.getSharedPreferences("gallery_prefs", Context.MODE_PRIVATE)
    private val editor = sharedPref.edit()

    fun clearSharedPrefs() {
        sharedPref.edit { clear() }
    }

    fun putString(key: String, value: String?) {
        editor.putString(key, value).commit()
    }

    fun putStringAsync(key: String, value: String?) {
        editor.putString(key, value).apply()
    }

    fun getString(key: String, defValue: String? = null): String =
        sharedPref.getString(key, defValue) ?: ""

    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).commit()
    }

    fun getBoolean(key: String, defValue: Boolean = false): Boolean =
        sharedPref.getBoolean(key, defValue)

    companion object {
        const val K_LAST_SEARCH_TERM = "last_search_term"
        const val K_AUTO_POLL_ENABLED = "auto_poll_enabled"
        const val K_LAST_PHOTO_ID = "last_photo_id"
    }
}