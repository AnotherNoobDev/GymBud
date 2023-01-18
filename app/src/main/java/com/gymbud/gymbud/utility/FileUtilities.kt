package com.gymbud.gymbud.utility

import android.content.Context
import android.os.Environment
import android.util.Log
import com.gymbud.gymbud.BuildConfig
import java.io.File
import java.io.FileWriter

private const val GYMBUD_FILE_EXTENSION = ".gymbud"

private const val TAG = "FileUtilities"

fun saveProgramToFile(context: Context, filename: String, serializedProgram: String): File {
    val externalFilesDirectory: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    val textFile = File(externalFilesDirectory, filename +  GYMBUD_FILE_EXTENSION)

    try {
        val writer = FileWriter(textFile)
        writer.append(serializedProgram)
        writer.flush()
        writer.close()
    } catch (e: Exception) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Failed to save program to file. Reason: " + e.message)
        }
    }

    return textFile
}