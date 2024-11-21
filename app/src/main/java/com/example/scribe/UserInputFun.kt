package com.example.scribe

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText

// Base
fun inputPrompt(applicationContext: Context, callback: (String?) -> Unit) {
    // Create an AlertDialog.Builder
    val builder = AlertDialog.Builder(applicationContext)
    builder.setTitle("Enter new name")

    // Set up the input
    var userInput: String? = null
    val input = EditText(applicationContext)
    builder.setView(input)

    // Set up the buttons
    builder.setPositiveButton("OK") { dialog, which ->
        val name = input.text.toString()
        // Do something with the user's input
        userInput = name
        // For example, display a toast with the input
        callback(name)
    }
    builder.setNegativeButton("Cancel") { dialog, which ->
        dialog.cancel()
        callback(null)
    }

    // Show the AlertDialog
    builder.show()
}