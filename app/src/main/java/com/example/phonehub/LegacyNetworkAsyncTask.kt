@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION", "UNUSED_PARAMETER", "unused")

package com.example.phonehub

import android.os.AsyncTask

@Deprecated("AsyncTask is deprecated in Android API 30+. Use Kotlin Coroutines instead.")
class LegacyNetworkAsyncTask : AsyncTask<String, Void, String>() {

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: String?): String {
        return try {
            "Legacy Data Loaded"
        } catch (e: Exception) {
            "Error loading legacy data"
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
    }
}