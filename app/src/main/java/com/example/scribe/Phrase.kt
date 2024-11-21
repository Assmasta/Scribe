package com.example.scribe

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Phrase(val body: String, val time: LocalDateTime)

@RequiresApi(Build.VERSION_CODES.O)
fun getPhrase(line: String?): Phrase {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val time: LocalDateTime = LocalDateTime.parse(line!!.split(",")[0])
    val body = line!!.split(",")[1]
    return Phrase(body, time)
}
