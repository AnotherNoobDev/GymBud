package com.example.gymbud.data

import android.content.Context
import androidx.room.*
import com.example.gymbud.model.*

class Converters {
    @TypeConverter
    fun fromId(value: ItemIdentifier?): Exercise? {
        return value?.let { Exercise(value) }
    }

    @TypeConverter
    fun exerciseToId(exercise: Exercise?): ItemIdentifier? {
        return exercise?.id
    }

    @TypeConverter
    fun fromString(value: String?): IntRange? {
        return value?.let {
            val tokenized = value.split("..")

            return@let if(tokenized.size != 2) {
                null
            } else {
                IntRange(tokenized[0].toInt(), tokenized[1].toInt())
            }
        }
    }


    @TypeConverter
    fun intRangeToString(range: IntRange?): String? {
        return range?.let { "${range.first}..${range.last}" }
    }
}



@Database(
    entities = [
        Exercise::class,
        ExerciseTemplate::class,
        RestPeriod::class,
        SetTemplate::class,
        SetTemplateWithItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GymBudRoomDatabase: RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseTemplateDao(): ExerciseTemplateDao
    abstract fun restPeriodDao(): RestPeriodDao
    abstract fun setTemplateDao(): SetTemplateDao
    abstract fun setTemplateWithItemDao(): SetTemplateWithItemDao

    companion object {
        // note: The value of a volatile variable will never be cached,
        // and all writes and reads will be done to and from the main memory.
        // This helps make sure the value of INSTANCE is always up-to-date and the same for all execution threads.
        // It means that changes made by one thread to INSTANCE are visible to all other threads immediately.
        @Volatile
        private var INSTANCE: GymBudRoomDatabase? = null

        fun getDatabase(context: Context): GymBudRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymBudRoomDatabase::class.java,
                    "gymbud_database"
                )
                    .fallbackToDestructiveMigration() // todo migration scheme for production: https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                    .build()

                INSTANCE = instance

                return instance
            }
        }
    }
}