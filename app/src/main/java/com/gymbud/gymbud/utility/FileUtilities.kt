package com.gymbud.gymbud.utility

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.gymbud.gymbud.BuildConfig
import java.io.*


const val GYMBUD_PROGRAM_FILE_EXTENSION = ".template.gymbud"

private const val TAG = "FileUtilities"


fun saveProgramToFile(context: Context, filename: String, serializedProgram: String): File {
    val externalFilesDirectory: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    val textFile = File(externalFilesDirectory, filename +  GYMBUD_PROGRAM_FILE_EXTENSION)

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


fun readFileContent(inputStream: InputStream): List<String> {
    val inputStreamReader = InputStreamReader(inputStream)
    val bufferedReader = BufferedReader(inputStreamReader)

    val content = mutableListOf<String>()
    var line: String? = bufferedReader.readLine()

    while (line != null) {
        content.add(line)
        line = bufferedReader.readLine()
    }

    bufferedReader.close()

    return content
}


fun distributeFile(file: File, context: Context, intentType: String, intentSubject: String, title: String): Intent {
    val fileUri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)

    context.grantUriPermission("android.content.ContentProvider", fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

    val intentShareFile = Intent(Intent.ACTION_SEND)

    intentShareFile.apply {
        type = intentType
        data = fileUri
        putExtra(Intent.EXTRA_STREAM, fileUri)
        putExtra(Intent.EXTRA_SUBJECT, intentSubject)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    return Intent.createChooser(intentShareFile, title)
}


fun copyFile(dstFile: File, srcFile: File) {
    val inStream = FileInputStream(srcFile)
    val outStream = FileOutputStream(dstFile)

    copyFile(outStream, inStream)
}


fun copyFile(outStream: OutputStream, inStream: InputStream) {
    // Transfer bytes from in to out
    val buf = ByteArray(1024)
    var len: Int
    while (inStream.read(buf).also { len = it } > 0) {
        outStream.write(buf, 0, len)
    }
}


