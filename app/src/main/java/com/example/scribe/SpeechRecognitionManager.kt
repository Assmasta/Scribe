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

class SpeechRecognitionManager(
    private val applicationContext: Context,
    private val sharedViewModel: SharedViewModel,
    ) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val speechRecognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    var isListening: Boolean = false

    init {
        initializeSpeechRecognizer()
        detailSRI(speechRecognizerIntent)
    }

    private fun initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
//        speechRecognizer?.setRecognitionListener(recognitionListener)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {
            }
            override fun onBeginningOfSpeech() {
            }
            override fun onRmsChanged(v: Float) {
            }
            override fun onBufferReceived(bytes: ByteArray?) {
            }
            override fun onEndOfSpeech() {
                // start speech recognition again
//                startSpeechRecognition()

                loopSRM()
                customToast(applicationContext, "end of speech")
            }

            override fun onError(i: Int) {
                // start speech recognition again
//                startSpeechRecognition()

                loopSRM()
                customToast(applicationContext,"input error")
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

                    sharedViewModel.stopRequested = output == "stop transcribing"
                    customToast(applicationContext, "onresults end")
                }

            }
            override fun onPartialResults(bundle: Bundle) {
            }
            override fun onEvent(i: Int, bundle: Bundle?) {
            }
        })
    }

    fun loopSRM() {
        if (!sharedViewModel.stopRequested) {
            // recurse
            startSpeechRecognition()
        }
        else {
            setFabAttributes(applicationContext, R.drawable.ic_mic, R.color.teal_200)
        }
    }

    fun startSpeechRecognition() {
        speechRecognizer?.startListening(speechRecognizerIntent)
    }

    fun stopSpeechRecognition() {
        if (isListening) {
            speechRecognizer?.stopListening()
            isListening = false
        }
    }

    private fun detailSRI(speechRecognizerIntent: Intent) {
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
        )
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault())

        // extra flexibility, for API 33 and up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SEGMENTED_SESSION, true)
        }
    }

    /*
    private val recognitionListener = object : RecognitionListener {
        // Implement recognitionListener functions here
        override fun onReadyForSpeech(params: Bundle?) {
        }
        override fun onBeginningOfSpeech() {
        }
        override fun onRmsChanged(rmsdB: Float) {
        }
        override fun onBufferReceived(buffer: ByteArray?) {
        }
        override fun onEndOfSpeech() {
            isListening = false
            // start speech recognition
            if (!sharedViewModel.stopRequested) {
                // TODO try loop
                startSpeechRecognition()
            }
        }

        override fun onError(error: Int) {
            customToast(applicationContext,"input error")
            isListening = false
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onResults(results: Bundle?) {
            val result = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
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
                isListening = false

                if (output == "stop transcribing") {
                    sharedViewModel.stopRequested = true
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
        }
        override fun onEvent(eventType: Int, params: Bundle?) {
        }
    }

     */
}