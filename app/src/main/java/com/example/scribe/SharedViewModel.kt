package com.example.scribe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

// for sharing variables
class SharedViewModel : ViewModel() {

    var isListening = false
    var stopRequested = true

    // selected on create
    var currentCSV: File? = null

    // livedata for text
    // establish livedata list
    private val _gData = MutableLiveData<List<String>>()
    val gData: LiveData<List<String>> get() = _gData

    // function to update data
    fun gDataSet(newValue: List<String>) {
        _gData.value = newValue
    }

    // measure length
    fun gDataSize() = gData.value?.size ?: 0

    // retrieves element from livedata list
    fun gDataIndex(index: Int): String? {
        return gData.value?.getOrNull(index)
    }

    // livedata for nav drawer
    // establish livedata list
    private val _gCSV = MutableLiveData<List<String>>()
    val gCSV: LiveData<List<String>> get() = _gCSV

    // function to update data
    fun gCSVSet(newValue: List<String>) {
        _gCSV.value = newValue
    }

    // measure length
    fun gCSVSize() = gCSV.value?.size ?: 0

    // retrieves element from livedata list
    fun gCSVIndex(index: Int): String? {
        return gCSV.value?.getOrNull(index)
    }
}