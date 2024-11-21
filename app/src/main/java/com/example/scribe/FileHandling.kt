package com.example.scribe

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

fun findDirectory(context: Context): File? {
    return context.getExternalFilesDir(null)
}

// TODO write replace
@RequiresApi(Build.VERSION_CODES.O)
fun getAllFilesInFolder(path: Path): List<String> {
    val fileList = mutableListOf<String>()
    Files.walk(path)
        .filter { item -> Files.isRegularFile(item) }
        .filter { item -> item.toString().endsWith(".csv") }
        .forEach { fileList
            .add(it
                .toString()
                .split("/")
                .last()
                .split(".csv")[0]
            )
        }
    return fileList.toList().asReversed()
}

// Base
fun fileToString(input: File?): String {
    return input
        .toString()
        .split("/")
        .last()
        .split(".csv")[0]
}

// TODO handle null input
// Base
fun stringToFile(input: String?, transcriptionfolder: File): File {
    return File(transcriptionfolder, "${input}.csv")
}

//// Base
//fun generateMenuItemsFromFiles(directory: File): List<Pair<String, File>> {
//    val files = directory.listFiles()?.filter { it.isFile } ?: emptyList()
//    return files.map { it.name to it }
//}
//
//// Base
//fun populateNavMenu(navView: NavigationView, directory: File) {
//    // Populate navigation menu dynamically
//    val menuItems = generateMenuItemsFromFiles(directory)
//    menuItems.forEach { (fileName, file) ->
//        navView.menu.add(Menu.NONE, Menu.NONE, Menu.NONE, fileName)
//    }
//}


