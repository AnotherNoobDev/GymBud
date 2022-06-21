package com.example.gymbud.data.datasource.database

import android.content.Context
import androidx.room.*
import com.example.gymbud.model.*

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

    private val delimiterBetweenFirstAndLast = ".."

    @TypeConverter
    fun intRangeFromString(value: String?): IntRange? {
        return value?.let {
            val tokenized = value.split(delimiterBetweenFirstAndLast)

            return@let if(tokenized.size != 2) {
                null
            } else {
                IntRange(tokenized[0].toInt(), tokenized[1].toInt())
            }
        }
    }

    @TypeConverter
    fun intRangeToString(range: IntRange?): String? {
        return range?.let { "${range.first}${delimiterBetweenFirstAndLast}${range.last}" }
    }


    /// Tags <--> String (tagCategory1:tagValue1,tagValue2,tagValue3|tagCategory2:tagValue1,tagValue2)

    private val delimiterBetweenCategories = "|"
    private val delimiterBetweenCategoryAndValues = ":"
    private val delimiterBetweenValues = ","

    @TypeConverter
    fun tagsFromString(value: String?): Tags? {
        return value?.let {
            val tags: MutableMap<TagCategory, Set<String>> = mutableMapOf()

            if (value.isEmpty()) return@let tags.toMap()

            value.split(delimiterBetweenCategories).forEach{ categoryWithValues ->
                val (category, values) = categoryWithValues.split(delimiterBetweenCategoryAndValues)
                tags[TagCategory.valueOf(category)] = values.split(delimiterBetweenValues).toSet()
            }

            return@let tags.toMap()
        }
    }

    @TypeConverter
    fun tagsToString(tags: Tags?): String? {
        return tags?.let {
            tags.entries.joinToString(separator = delimiterBetweenCategories) {
                "${it.key}${delimiterBetweenCategoryAndValues}${it.value.joinToString(separator=delimiterBetweenValues)}"
            }
        }
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
                    .fallbackToDestructiveMigration() // todo migration scheme for production: https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                    .build()

                INSTANCE = instance

                return instance
            }
        }
    }
}