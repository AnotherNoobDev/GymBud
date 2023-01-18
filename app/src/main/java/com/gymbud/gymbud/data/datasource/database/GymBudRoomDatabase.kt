package com.gymbud.gymbud.data.datasource.database

import android.content.Context
import androidx.room.*
import com.gymbud.gymbud.model.*
import com.gymbud.gymbud.utility.convertIntRangeFromString
import com.gymbud.gymbud.utility.convertIntRangeToString
import com.gymbud.gymbud.utility.convertTagsFromString
import com.gymbud.gymbud.utility.convertTagsToString

class Converters {
    /// Exercise <--> ItemIdentifier

    @TypeConverter
    fun exerciseFromId(value: ItemIdentifier?): Exercise? {
        return value?.let { Exercise(value) }
    }

    @TypeConverter
    fun exerciseToId(exercise: Exercise?): ItemIdentifier? {
        return exercise?.id
    }


    /// IntRange <--> String (first..last)

    @TypeConverter
    fun intRangeFromString(value: String?): IntRange? {
        return convertIntRangeFromString(value)
    }

    @TypeConverter
    fun intRangeToString(range: IntRange?): String? {
        return convertIntRangeToString(range)
    }


    /// Tags <--> String (tagCategory1:tagValue1,tagValue2,tagValue3|tagCategory2:tagValue1,tagValue2)

    @TypeConverter
    fun tagsFromString(value: String?): Tags? {
        return convertTagsFromString(value)
    }

    @TypeConverter
    fun tagsToString(tags: Tags?): String? {
        return convertTagsToString(tags)
    }
}



@Database(
    entities = [
        Exercise::class,
        ExerciseSessionRecord::class,
        ExerciseTemplate::class,
        ProgramTemplate::class,
        ProgramTemplateWithItem::class,
        RestPeriod::class,
        SetTemplate::class,
        SetTemplateWithItem::class,
        WorkoutSessionRecord::class,
        WorkoutTemplate::class,
        WorkoutTemplateWithItem::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GymBudRoomDatabase: RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseSessionRecordDao(): ExerciseSessionRecordDao
    abstract fun exerciseTemplateDao(): ExerciseTemplateDao
    abstract fun programTemplateDao(): ProgramTemplateDao
    abstract fun programTemplateWithItemDao(): ProgramTemplateWithItemDao
    abstract fun restPeriodDao(): RestPeriodDao
    abstract fun setTemplateDao(): SetTemplateDao
    abstract fun setTemplateWithItemDao(): SetTemplateWithItemDao
    abstract fun workoutSessionRecordDao(): WorkoutSessionRecordDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun workoutTemplateWithItemDao(): WorkoutTemplateWithItemDao


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
                    .build()

                INSTANCE = instance

                return instance
            }
        }
    }
}