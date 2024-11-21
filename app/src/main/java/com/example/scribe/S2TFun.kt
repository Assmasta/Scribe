package com.example.scribe

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.util.Locale

import com.example.scribe.databinding.ActivityMainBinding

// TODO relegate to background
// Base
fun startSpeechToText(
    applicationContext: Context,
    sharedViewModel: SharedViewModel,
) {
    customToast(applicationContext, "start speechtotext")
    var speechRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
    val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    speechRecognizerIntent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
    )

    // extra flexibility, for API 33 and up
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SEGMENTED_SESSION, true)
    }

    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

    speechRecognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(bundle: Bundle?) {
        }
        override fun onBeginningOfSpeech() {
        }
        override fun onRmsChanged(v: Float) {
        }
        override fun onBufferReceived(bytes: ByteArray?) {
        }
        override fun onEndOfSpeech() {
            startSpeechToText(applicationContext, sharedViewModel)
            sharedViewModel.isListening = false
        }

        override fun onError(i: Int) {
            customToast(applicationContext,"input error")
            startSpeechToText(applicationContext, sharedViewModel)
            sharedViewModel.isListening = false
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onResults(bundle: Bundle) {
            val result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (result != null) {
                // write (append) to selected csv
                val output: String = result[0]

                // write output to CSV
                writeCSV(
                    sharedViewModel.currentCSV,
                    output,
                    LocalDateTime.now()
                )

                // update gdata
                sharedViewModel.gDataSet(readCSV(sharedViewModel.currentCSV))
                sharedViewModel.isListening = false

                // TODO stop
                if (output == "stop transcribing") {
                    sharedViewModel.stopRequested = true
                }
            }
        }
        override fun onPartialResults(bundle: Bundle) {
        }
        override fun onEvent(i: Int, bundle: Bundle?) {
        }
    })
    speechRecognizer.startListening(speechRecognizerIntent)
}

// Base
fun stopSpeechRecognition(speechRecognizer: SpeechRecognizer) {
    speechRecognizer.setRecognitionListener(null) // Unregister the listener
    speechRecognizer.stopListening()
    speechRecognizer.cancel()
    speechRecognizer.destroy() // Release the SpeechRecognizer instance
    // TODO
//    speechRecognizer = null // Set the reference to null
}

// Call this method when you want to stop speech recognition
fun onUserRequestToStopListening(speechRecognizer: SpeechRecognizer) {
    stopSpeechRecognition(speechRecognizer)
    // Handle any additional logic
}

//            micIV.setColorFilter(ContextCompat.getColor(applicationContext, R.color.mic_enabled_color)) // #FF0E87E7
//            micIV.setColorFilter(ContextCompat.getColor(applicationContext, R.color.mic_disabled_color)) // #FF6D6A6A


// former write
//                val path = findDirectory(applicationContext)
//                val transcriptionfolder = File(path, "Transcriptions")
//                val file = File(transcriptionfolder, "example.csv")
//                val dt = LocalDateTime.now()