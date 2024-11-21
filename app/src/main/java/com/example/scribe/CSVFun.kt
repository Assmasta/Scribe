package com.example.scribe

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.FileAlreadyExistsException
import java.nio.file.NoSuchFileException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Base
@RequiresApi(Build.VERSION_CODES.O)
fun newCSV(targetDirectory: File, sharedViewModel: SharedViewModel) {
    val dt = LocalDateTime.now()
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy_HH-mm-ss")
    val time = dt.format(formatter)
    createCSV(targetDirectory,time)
    // TODO get name and set to current
    sharedViewModel.currentCSV = stringToFile(time, targetDirectory)
}

// Base
fun createCSV(targetDirectory: File, fileName: String) {
    val file = File(targetDirectory, "$fileName.csv")
    file.createNewFile()
}

// Base
fun deleteCSV(deletionTarget: File?): Boolean {
    if (deletionTarget != null) {
        return deletionTarget.delete()
    }
    else {
        return false
    }
}

// Base
@RequiresApi(Build.VERSION_CODES.O)
fun renameCSV(applicationContext: Context, targetDirectory: File, sharedViewModel: SharedViewModel) {
    // identify target file, hardcoded to currentCSV
    val renameTarget = sharedViewModel.currentCSV
    inputPrompt(applicationContext) { userInput ->
        // upon receiving user input via prompt
        if ((userInput != null && renameTarget != null)) {
            // TODO fix for throw (crashes when thrown)
            // retrieve path
            val intendedPath = stringToFile("$userInput", targetDirectory)
            try {
                if (!renameTarget.exists()) {
                    customToast(applicationContext, "Source file doesn't exist")
                    throw NoSuchFileException("Source file doesn't exist")
                }
                val check = intendedPath // File(intendedName)
                if (check.exists()) {
                    customToast(applicationContext, "Destination file already exists")
                    throw FileAlreadyExistsException("Destination file already exists")
                }
                val result = renameTarget.renameTo(check)
            } catch (_: NoSuchFileException) {
            } catch (_: FileAlreadyExistsException) {
            } finally {
                // update drawer
                sharedViewModel.gCSVSet(getAllFilesInFolder(targetDirectory.toPath()))
            }
        }
    }
}

fun readCSV(file: File?): List<String> {
    val output = mutableListOf<String>()
    if (file != null) {
        BufferedReader(FileReader(file)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                // unloads timestamp + text output
                line?.let {
                    output.add(it)
                }
            }
        }
    }
    return output
}

@RequiresApi(Build.VERSION_CODES.O)
fun writeCSV(file: File?, insertText: String, dt: LocalDateTime) {
    if (file != null) {
        // removing true will just erase file contents beforehand
        val writer = BufferedWriter(FileWriter(file, true))
        writer.write("${dt},${insertText}")
        writer.newLine()
        writer.close()
    }
}