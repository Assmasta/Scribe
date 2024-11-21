package com.example.scribe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
/**
 * Converts a List of Strings to a LiveData object containing the same list.
 *
 * @receiver List<String> The list of strings to be converted.
 * @return LiveData<List<String>> A LiveData object encapsulating the original list.
 */
fun List<String>.toLiveData(): LiveData<List<String>> {
    val liveData = MutableLiveData<List<String>>()
    liveData.value = this
    return liveData
}
