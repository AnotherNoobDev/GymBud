package com.gymbud.gymbud.utility

import android.os.Environment
import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.gymbud.gymbud.AppConfig
import com.gymbud.gymbud.BaseApplication
import com.gymbud.gymbud.BuildConfig
import com.gymbud.gymbud.data.ItemIdentifierGenerator
import kotlinx.coroutines.runBlocking
import java.io.*
import java.util.*
import kotlin.math.max


private const val TAG = "Restoration"

const val GYMBUD_BACKUP_FILE_EXTENSION = ".bak.gymbud"

private const val WAL_FILE = "wal"
private const val SHM_FILE = "shm"

class BackupException(message: String): Exception(message)
class RestorationException(message: String): Exception(message)


private class BackupFileFilter: FilenameFilter {
    override fun accept(dir: File?, filename: String?): Boolean {
        if (filename != null) {
            return filename.endsWith(GYMBUD_BACKUP_FILE_EXTENSION)
        }

        return false
    }
}


private class DatabaseTmpFilesFilter: FilenameFilter {
    override fun accept(dir: File?, filename: String?): Boolean {
        if (filename != null) {
            return (filename.contains(WAL_FILE) || filename.contains(SHM_FILE))
        }

        return false
    }
}



fun createBackup(app: BaseApplication): File {
    val externalFilesDirectory: File? = app.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    if (externalFilesDirectory == null) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Failed to create backup. Reason: Failed to open external files dir.")
        }

        throw BackupException("Failed to create backup.")
    }

    // remove previous backup if any
    externalFilesDirectory.listFiles(BackupFileFilter())?.forEach {
        it.delete()
    }

    // trigger db checkpoint to ensure db file has all changes (from wal etc.)
    val c = app.database.query(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
    if(c.moveToFirst() && c.getInt(0) == 1) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Failed to create backup. Reason: Failed to execute pragma wal_checkpoint(full).")
        }

        throw BackupException("Failed to create backup.")
    }

    // todo
    // If the SQLite database file is intended to be transmitted over a network,
    // then the vacuum command should be run after checkpoint.
    // This removes the fragmentation in the database file thereby reducing its size, so you transfer less data through network.

    // make backup
    val dbFile = app.getDatabasePath(AppConfig.DATABASE_NAME)

    val ts = TimeFormatter.getFormattedDateDDMMYYYY(Date()).replace('/', '_').replace('\\', '_')
    val bakFileName = "GymBud_$ts$GYMBUD_BACKUP_FILE_EXTENSION"
    val bakFile = File(externalFilesDirectory, bakFileName)

    try {
        copyFile(bakFile, dbFile)
    } catch(e: Exception) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Failed to create backup. Reason: " + e.message)
        }

        throw BackupException("Failed to create backup.")
    }

    return bakFile
}


suspend fun restoreFromBackup(app: BaseApplication, restore: InputStream) {
    // create backup before attempting restore
    val bakFile = try {
        createBackup(app)
    } catch (e: Exception) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, e.stackTrace.toString())
        }

        throw RestorationException("Failed to setup backup of local data. Restoration will not be attempted.")
    }

    // if fail .. restore from backup
    // todo if restore from backup fails should we reset db? drop all tables..
    var success = true

    val dbFile = app.getDatabasePath(AppConfig.DATABASE_NAME)
    app.database.close()

    // remove db tmp files
    val tmpFiles = dbFile.parentFile?.listFiles(DatabaseTmpFilesFilter())
    tmpFiles?.forEach {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Deleting db tmp file ${it.absolutePath}")
        }

        it.delete()
    }

    // copy restore file over db file

    try {
        @Suppress("BlockingMethodInNonBlockingContext")
        val outStream = FileOutputStream(dbFile)

        copyFile(outStream, restore)
    } catch(e: Exception) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Failed to copy from restore file to db file: " + e.message)
        }

        success = false
    }

    if (!success) {
        revertRestore(dbFile, bakFile)
    }

    // test db is valid
    app.resetDbConnection()

    if(!app.database.openHelper.readableDatabase.isDatabaseIntegrityOk) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Database integrity check  failed")
        }

        success = false
    }

    if (!success) {
        revertRestore(dbFile, bakFile)
    }

    // re-seed ItemIdentifierGenerator
    val maxId = max(app.sessionRepository.getMaxId(), app.itemRepository.getMaxId())

    @Suppress("BlockingMethodInNonBlockingContext", "BlockingMethodInNonBlockingContext")
    runBlocking {
        app.appRepository.updateLastUsedItemIdentifier(maxId)
    }

    ItemIdentifierGenerator.reset()

    val nextId = ItemIdentifierGenerator.generateId()
    if (nextId != (maxId + 1)) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Restore failed! Generated id: $nextId. Expected: ${maxId + 1}")
        }

        success = false
    }

    if (!success) {
        revertRestore(dbFile, bakFile)
    }

    // reset DataStore entries related to DB
    app.appRepository.updateActiveProgramAndProgramDay(ItemIdentifierGenerator.NO_ID, ItemIdentifierGenerator.NO_ID)
    app.appRepository.clearPartialWorkoutSessionInfo()
}


private fun revertRestore(dbFile: File, bakFile: File) {
    try {
        copyFile(dbFile, bakFile)
    } catch(e: Exception) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Restore operation failed and restore from backup also failed: " + e.message)
        }

        throw SerializationException("Restore failed! Local data may have been corrupted!")
    }

    throw SerializationException("Restore failed! Local data was not corrupted.")
}