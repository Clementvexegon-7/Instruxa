package com.clementvexegon.instruxa.data.network

import android.content.Context
import com.cloudinary.android.MediaManager

object CloudinaryConfig {

    fun init(context: Context) {
        val config = mapOf(
            "cloud_name" to "YOUR_CLOUD_NAME",
            "api_key"    to "YOUR_API_KEY",
            "api_secret" to "YOUR_API_SECRET"
        )
        MediaManager.init(context, config)
    }

    // Call this to upload any image
    // Returns the secure URL of the uploaded image
    fun uploadImage(
        filePath: String,
        folder: String = "instruxa/portfolio",
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        MediaManager.get().upload(filePath)
            .option("folder", folder)
            .callback(object : com.cloudinary.android.callback.UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String ?: ""
                    onSuccess(url)
                }
                override fun onError(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {
                    onError(error.description ?: "Unknown error")
                }
                override fun onReschedule(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {}
            })
            .dispatch()
    }
}