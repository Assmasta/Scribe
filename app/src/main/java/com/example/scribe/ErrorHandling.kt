package com.example.scribe

import android.content.Context
import android.util.Log
import android.widget.Toast

fun speechRecognizerErrorRetriever(i: Int): String {
    val errors = arrayListOf<String>(
        "Network Timeout",
        "Network",
        "Audio",
        "Server",
        "Client",
        "Timeout",
        "No Match",
        "Recognizer Busy",
        "Insufficient Permissions",
        "Network Setup Failure"
    )
    return errors[i - 1]
}

// Base
fun customToast(applicationContext: Context, message: String) {
    Toast.makeText(
        applicationContext,
        message,
        Toast.LENGTH_SHORT)
        .show()
    Log.e("customToast", "customToast: $message", )
}