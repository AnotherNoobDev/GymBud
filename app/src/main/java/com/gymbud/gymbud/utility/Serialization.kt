package com.gymbud.gymbud.utility

import android.database.Cursor
import android.util.Log
import com.gymbud.gymbud.BaseApplication
import com.gymbud.gymbud.BuildConfig
import com.gymbud.gymbud.data.ItemIdentifierGenerator
import com.gymbud.gymbud.data.datasource.database.GymBudRoomDatabase
import com.gymbud.gymbud.data.repository.*
import com.gymbud.gymbud.model.*
import kotlin.NumberFormatException

private const val TAG = "Serialization"

class SerializationException(message: String): Exception(message)


/**
 * Format is as follows
 *
 * TABLE_DELIMITERtable_name_1:
 * col_name_1SERIALIZATION_DELIMITERcol_name_1COLUMN_DELIMITER...col_name_n
 * ROW_DELIMITERrow_1_col_1SERIALIZATION_DELIMITERrow_1_col2COLUMN_DELIMITER..row_1_col_n
 * ..
 * ROW_DELIMITERrow_m_col_1SERIALIZATION_DELIMITERrow_m_col2COLUMN_DELIMITER..row_m_col_n
 *
 * TABLE_DELIMITERtable_name_2:
 * ..
 *
 *
 * Notes:
 *     - table starts with a line TABLE_DELIMITERtable_name:
 *     - next line contains the column names
 *     - the next lines after that contain the rows with values
 */

private typealias SerializationInterimRes = Pair<String, Map<String, Set<ItemIdentifier>>>

private const val COLUMN_DELIMITER = "#!#"
private const val ROW_DELIMITER = "#$#"
private const val TABLE_DELIMITER = "#@#"

private const val SECTION_VERSION = "VERSION"

private const val SECTION_PROGRAM_TEMPLATE = "program_template"
private const val SECTION_PROGRAM_TEMPLATE_ITEM = "program_template_item"
private const val SECTION_WORKOUT_TEMPLATE = "workout_template"
private const val SECTION_WORKOUT_TEMPLATE_ITEM = "workout_template_item"
private const val SECTION_SET_TEMPLATE = "set_template"
private const val SECTION_SET_TEMPLATE_ITEM = "set_template_item"
private const val SECTION_EXERCISE_TEMPLATE = "exercise_template"
private const val SECTION_EXERCISE = "exercise"
private const val SECTION_REST_PERIOD = "rest_period"


fun serializeProgramTemplate(programTemplate: ItemIdentifier, database: GymBudRoomDatabase): String {
    val version = database.openHelper.readableDatabase.version.toString()

    var serialization = "$SECTION_VERSION: $version\n\n"

    // collect rest periods as we go and retrieve them at the end
    val restPeriods = mutableSetOf<ItemIdentifier>()

    var res = serializeProgramTemplateHeader(programTemplate, database)
    serialization += res.first
    restPeriods.addAll(res.second["rest_period_id"]?:setOf())

    val workouts = res.second["workout_template_id"]?: setOf()
    res = serializeWorkoutTemplates(workouts, database)
    serialization += res.first
    restPeriods.addAll(res.second["rest_period_id"]?:setOf())

    val sets = res.second["set_template_id"]?: setOf()
    res = serializeSetTemplates(sets, database)
    serialization += res.first
    restPeriods.addAll(res.second["rest_period_id"]?:setOf())

    val exerciseTemplates = res.second["exercise_template_id"]?: setOf()
    res = serializeExerciseTemplates(exerciseTemplates, database)
    serialization += res.first

    val exercises = res.second["exercise_id"]?: setOf()
    res = serializeExercises(exercises, database)
    serialization += res.first

    res = serializeRestPeriods(restPeriods, database)
    serialization += res.first

    return serialization
}


private fun serializeProgramTemplateHeader(programTemplate: ItemIdentifier, database: GymBudRoomDatabase): SerializationInterimRes {
    var serialization = ""

    var res = serializeProgramTemplateContainer(programTemplate, database)
    serialization += res.first

    res = serializeProgramTemplateItems(programTemplate, database)
    serialization += res.first

    return Pair(serialization, res.second)
}


private fun serializeProgramTemplateContainer(programTemplate: ItemIdentifier, database: GymBudRoomDatabase): SerializationInterimRes {
    var serialization = ""
    serialization += TABLE_DELIMITER + "program_template:\n"

    val cursor = database.programTemplateDao().getRows(programTemplate)
    serialization += processCursorColumnNames(cursor)
    val res = processCursor(cursor, listOf())
    serialization += res.first

    serialization += "\n"

    return Pair(serialization, res.second)
}


private fun serializeProgramTemplateItems(programTemplate: ItemIdentifier, database: GymBudRoomDatabase): SerializationInterimRes {
    var serialization = ""
    serialization += TABLE_DELIMITER + "program_template_item:\n"

    val cursor = database.programTemplateWithItemDao().getRows(programTemplate)
    serialization += processCursorColumnNames(cursor)
    val res = processCursor(cursor, listOf("workout_template_id", "rest_period_id"))
    serialization += res.first

    serialization += "\n"

    return Pair(serialization, res.second)
}


private fun serializeWorkoutTemplates(workoutTemplates: Set<ItemIdentifier>, database: GymBudRoomDatabase): SerializationInterimRes {
    var serialization = ""

    var res = serializeWorkoutTemplateContainers(workoutTemplates, database)
    serialization += res.first

    res = serializeWorkoutTemplateContainerItems(workoutTemplates, database)
    serialization += res.first

    return Pair(serialization, res.second)
}


private fun serializeWorkoutTemplateContainers(workoutTemplates: Set<ItemIdentifier>, database: GymBudRoomDatabase): SerializationInterimRes {
    var serialization = ""
    serialization += TABLE_DELIMITER + "workout_template:\n"

    serialization += processCursorColumnNames(database.workoutTemplateDao().getRows(workoutTemplates.first()))

    for (id in workoutTemplates.sorted()) {
        val cursor = database.workoutTemplateDao().getRows(id)
        val res = processCursor(cursor, listOf())
        serialization += res.first
    }

    serialization += "\n"

    return Pair(serialization, mapOf())
}


private fun serializeWorkoutTemplateContainerItems(workoutTemplates: Set<ItemIdentifier>, database: GymBudRoomDatabase): SerializationInterimRes {
    var serialization = ""
    serialization += TABLE_DELIMITER + "workout_template_item:\n"

    serialization += processCursorColumnNames(database.workoutTemplateWithItemDao().getRows(workoutTemplates.first()))

    val restPeriods = mutableSetOf<ItemIdentifier>()
    val sets = mutableSetOf<ItemIdentifier>()

    for (id in workoutTemplates.sorted()) {
        val cursor = database.workoutTemplateWithItemDao().getRows(id)
        val res = processCursor(cursor, listOf("set_template_id", "rest_period_id"))
        serialization += res.first

        restPeriods.addAll(res.second["rest_period_id"]?: listOf())
        sets.addAll(res.second["set_template_id"]?: listOf())
    }

    serialization += "\n"

    return Pair(serialization, mapOf("rest_period_id" to restPeriods.toSet(), "set_template_id" to sets.toSet()))
}


private fun serializeSetTemplates(setTemplates: Set<ItemIdentifier>, database: GymBudRoomDatabase): SerializationInterimRes {
    var serialization = ""

    var res = serializeSetTemplateContainers(setTemplates, database)
    serialization += res.first

    res = serializeSetTemplateContainerItems(setTemplates, database)
    serialization += res.first

    return Pair(serialization, res.second)
}


private fun serializeSetTemplateContainers(setTemplates: Set<ItemIdentifier>, database: GymBudRoomDatabase): SerializationInterimRes {
    var serialization = ""
    serialization += TABLE_DELIMITER + "set_template:\n"

    serialization += processCursorColumnNames(database.setTemplateDao().getRows(setTemplates.first()))

    for (id in setTemplates.sorted()) {
        val cursor = database.setTemplateDao().getRows(id)
        val res = processCursor(cursor, listOf())
        serialization += res.first
    }

    serialization += "\n"

    return Pair(serialization, mapOf())
}


private fun serializeSetTemplateContainerItems(setTemplates: Set<ItemIdentifier>, database: GymBudRoomDatabase): SerializationInterimRes {
    var serialization = ""
    serialization += TABLE_DELIMITER + "set_template_item:\n"

    serialization += processCursorColumnNames(database.setTemplateWithItemDao().getRows(setTemplates.first()))

    val restPeriods = mutableSetOf<ItemIdentifier>()
    val exerciseTemplates = mutableSetOf<ItemIdentifier>()

    for (id in setTemplates.sorted()) {
        val cursor = database.setTemplateWithItemDao().getRows(id)
        val res = processCursor(cursor, listOf("exercise_template_id", "rest_period_id"))
        serialization += res.first

        restPeriods.addAll(res.second["rest_period_id"]?: listOf())
        exerciseTemplates.addAll(res.second["exercise_template_id"]?: listOf())
    }

    serialization += "\n"

    return Pair(serialization, mapOf("rest_period_id" to restPeriods.toSet(), "exercise_template_id" to exerciseTemplates.toSet()))
}


private fun serializeExerciseTemplates(exerciseTemplates: Set<ItemIdentifier>, database: GymBudRoomDatabase): SerializationInterimRes {
    var serialization = ""
    serialization += TABLE_DELIMITER + "exercise_template:\n"

    serialization += processCursorColumnNames(database.exerciseTemplateDao().getRows(exerciseTemplates.first()))

    val exercises = mutableSetOf<ItemIdentifier>()

    for (id in exerciseTemplates.sorted()) {
        val cursor = database.exerciseTemplateDao().getRows(id)
        val res = processCursor(cursor, listOf("exercise_id"))
        serialization += res.first

        exercises.addAll(res.second["exercise_id"]?: listOf())
    }

    serialization += "\n"

    return Pair(serialization, mapOf("exercise_id" to exercises.toSet()))
}


private fun serializeExercises(exercises: Set<ItemIdentifier>, database: GymBudRoomDatabase): SerializationInterimRes {
    var serialization = ""
    serialization += TABLE_DELIMITER + "exercise:\n"

    serialization += processCursorColumnNames(database.exerciseDao().getRows(exercises.first()))

    for (id in exercises.sorted()) {
        val cursor = database.exerciseDao().getRows(id)
        val res = processCursor(cursor, listOf())
        serialization += res.first
    }

    serialization += "\n"

    return Pair(serialization, mapOf())
}


private fun serializeRestPeriods(restPeriods: Set<ItemIdentifier>, database: GymBudRoomDatabase): SerializationInterimRes {
    var serialization = ""
    serialization += TABLE_DELIMITER + "rest_period:\n"

    serialization += processCursorColumnNames(database.restPeriodDao().getRows(restPeriods.first()))

    for (id in restPeriods.sorted()) {
        val cursor = database.restPeriodDao().getRows(id)
        val res = processCursor(cursor, listOf())
        serialization += res.first
    }

    serialization += "\n"

    return Pair(serialization, mapOf())
}


private fun processCursorColumnNames(cursor: Cursor): String {
    var serialization = ""

    cursor.moveToFirst()

    for (iColumn in 0 until cursor.columnCount - 1) {
        serialization += cursor.getColumnName(iColumn) + COLUMN_DELIMITER
    }

    serialization += cursor.getColumnName(cursor.columnCount - 1) + "\n"

    return serialization
}


/**
 * Returns the serialization of the rows pointed to by cursor
 * and the Ids stored in the idColumnsToTrack
 */
private fun processCursor(cursor: Cursor, idColumnsToTrack: List<String>): SerializationInterimRes {
    var serialization = ""
    val ids = mutableMapOf<String, MutableSet<ItemIdentifier>>()

    for (col in idColumnsToTrack) {
        ids[col] = mutableSetOf()
    }

    cursor.moveToFirst()
    while(!cursor.isAfterLast) {
        var rowSerialization = ROW_DELIMITER

        for (iColumn in 0 until cursor.columnCount - 1) {
            val colName = cursor.getColumnName(iColumn)
            if (colName in idColumnsToTrack) {
                ids[colName]!!.add(cursor.getLong(iColumn))
            }

            rowSerialization += cursor.getString(iColumn) + COLUMN_DELIMITER
        }

        val colName = cursor.getColumnName(cursor.columnCount - 1)
        if (colName in idColumnsToTrack) {
            ids[colName]!!.add(cursor.getLong(cursor.columnCount - 1))
        }

        rowSerialization += cursor.getString(cursor.columnCount - 1) + "\n"

        serialization += rowSerialization

        cursor.moveToNext()
    }

    return Pair(serialization, ids)
}


suspend fun deserializeProgramTemplate(serialization: List<String>, app: BaseApplication): Pair<Boolean, String> {
    if(!validateVersion(serialization, app.database)) {
        throw SerializationException("File version not supported.")
    }

    // locate sections
    val sections = locateSections(serialization)

    // make sure all sections are present
    // note: rest period section is optional, rest are mandatory
    val mandatorySections = listOf(
        SECTION_PROGRAM_TEMPLATE,
        SECTION_PROGRAM_TEMPLATE_ITEM,
        SECTION_WORKOUT_TEMPLATE,
        SECTION_WORKOUT_TEMPLATE_ITEM,
        SECTION_SET_TEMPLATE,
        SECTION_SET_TEMPLATE_ITEM,
        SECTION_EXERCISE_TEMPLATE,
        SECTION_EXERCISE
    )

    mandatorySections.forEach {
        if (!sections.containsKey(it)) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to locate section: $it")
                Log.d(TAG, sections.toString())
            }

            throw SerializationException("File is corrupt.")
        }
    }

    // parse sections
    val restPeriods = if(sections.containsKey(SECTION_EXERCISE)) {
        parseRestPeriodSection(serialization, sections[SECTION_REST_PERIOD]!!)
    } else {
        mutableMapOf()
    }

    val exercises = parseExerciseSection(serialization, sections[SECTION_EXERCISE]!!)
    val exerciseTemplates = parseExerciseTemplateSection(serialization, sections[SECTION_EXERCISE_TEMPLATE]!!)
    val setTemplateItems = parseSetTemplateItemSection(serialization, sections[SECTION_SET_TEMPLATE_ITEM]!!)
    val setTemplates = parseSetTemplateSection(serialization, sections[SECTION_SET_TEMPLATE]!!)
    val workoutTemplateItems = parseWorkoutTemplateItemSection(serialization, sections[SECTION_WORKOUT_TEMPLATE_ITEM]!!)
    val workoutTemplates = parseWorkoutTemplateSection(serialization, sections[SECTION_WORKOUT_TEMPLATE]!!)
    val programTemplateItems = parseProgramTemplateItemSection(serialization, sections[SECTION_PROGRAM_TEMPLATE_ITEM]!!)
    val programTemplate = parseProgramTemplateSection(serialization, sections[SECTION_PROGRAM_TEMPLATE]!!)

    // make sure all Ids are present from top (program) to bottom
    if (!isValidProgramTemplateItems(programTemplateItems, programTemplate, workoutTemplates, restPeriods) ||
        !isValidWorkoutTemplateItems(workoutTemplateItems, workoutTemplates, setTemplates, restPeriods) ||
        !isValidSetTemplateItems(setTemplateItems, setTemplates, exerciseTemplates, restPeriods) ||
        !isValidExerciseTemplates(exerciseTemplates, exercises)) {

            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to validate Ids")
            }

            throw SerializationException("File is corrupt.")
    }

    // add sections to each respective table from bottom to top
    // this section shall not fail and leave the db in a 'bad state' --> todo make a backup before importing and restore on failure?
    var nAddedItems = 0
    nAddedItems += addRestPeriods(restPeriods, app.restPeriodRepository)
    nAddedItems += addExercises(exercises, app.exerciseRepository)
    nAddedItems += addExerciseTemplates(exerciseTemplates, exercises, app.exerciseTemplateRepository)
    nAddedItems += addSetTemplates(setTemplates, setTemplateItems, exerciseTemplates, restPeriods, app.setTemplateRepository)
    nAddedItems += addWorkoutTemplates(workoutTemplates, workoutTemplateItems, setTemplates, restPeriods, app.workoutTemplateRepository)
    val (wasAdded, addedProgramTemplate) = addProgramTemplate(programTemplate, programTemplateItems, workoutTemplates, restPeriods, app.programRepository)

    if (nAddedItems == 0 && !wasAdded) {
        return Pair(false, "")
    }

    return Pair(true, addedProgramTemplate.name)
}


private fun validateVersion(serialization: List<String>, database: GymBudRoomDatabase): Boolean {
    if (serialization.isEmpty()) {
        return false
    }

    val tokens = serialization[0].split(":")
    if (tokens.size != 2) {
        return false
    }

    if (tokens[0].trim() != SECTION_VERSION) {
        return false
    }

    val dbVersion = database.openHelper.readableDatabase.version.toString()
    if (tokens[1].trim() != dbVersion) {
        return false
    }

    return true
}


private fun locateSections(serialization: List<String>): Map<String, Pair<Int, Int>> {
    val sectionStarts = mutableListOf<Int>()

    serialization.forEachIndexed { index, line  ->
        if (line.contains(TABLE_DELIMITER)) {
            sectionStarts.add(index)
        }
    }

    val res = mutableMapOf<String, Pair<Int, Int>>()

    for (i in 0 until sectionStarts.size - 1) {
        val name = serialization[sectionStarts[i]].trim().substring(TABLE_DELIMITER.length).removeSuffix(":")
        res[name] = Pair(sectionStarts[i] + 1, sectionStarts[i+1] - 2) // skip section header and go until next section header (+ drop empty line)
    }

    val name = serialization[sectionStarts.last()].trim().substring(TABLE_DELIMITER.length).removeSuffix(":")
    res[name] = Pair(sectionStarts.last() + 1, serialization.size - 2)

    return res
}


private fun parseRestPeriodSection(serialization: List<String>, loc: Pair<Int, Int>): MutableMap<ItemIdentifier, RestPeriod> {
    val res = mutableMapOf<ItemIdentifier, RestPeriod>()

    val columns = listOf("id", "name", "target_in_seconds")
    val values = extractColumns(serialization, loc, columns)

    // parse values from strings to attributes..
    val nEntries = values[0].size

    for (i in 0 until nEntries) {
        val id = try {
            values[0][i].toLong()
        } catch (e: NumberFormatException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "RestPeriod: Failed to parse id")
            }
            throw SerializationException("File is corrupt.")
        }

        val name = values[1][i]

        val targetInSeconds = convertIntRangeFromString(values[2][i])
        if (targetInSeconds == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "RestPeriod: Failed to parse id")
            }

            throw SerializationException("File is corrupt.")
        }

        res[id] = RestPeriod(id, name, targetInSeconds)
    }

    return res
}


private fun parseExerciseSection(serialization: List<String>, loc: Pair<Int, Int>): MutableMap<ItemIdentifier, Exercise> {
    val res = mutableMapOf<ItemIdentifier, Exercise>()

    val columns = listOf("id", "name", "notes", "target_muscle", "video_tutorial")
    val values = extractColumns(serialization, loc, columns)

    // parse values from strings to attributes..
    val nEntries = values[0].size

    for (i in 0 until nEntries) {
        val id = try {
            values[0][i].toLong()
        } catch (e: NumberFormatException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Exercise: Failed to parse id")
            }
            throw SerializationException("File is corrupt.")
        }

        val name = values[1][i]
        val notes = values[2][i]
        val targetMuscle = try {
            MuscleGroup.valueOf(values[3][i])
        } catch (e: IllegalArgumentException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Exercise: Failed to parse target_muscle. Row entry is: $values")
            }

            throw SerializationException("File is corrupt.")
        }

        val video = values[4][i]

        res[id] = Exercise(id, name, notes, targetMuscle, video)
    }

    return res
}


private fun parseExerciseTemplateSection(serialization: List<String>, loc: Pair<Int, Int>): MutableMap<ItemIdentifier, ExerciseTemplate> {
    val res = mutableMapOf<ItemIdentifier, ExerciseTemplate>()

    val columns = listOf("id", "name", "exercise_id", "target_rep_range")
    val values = extractColumns(serialization, loc, columns)

    // parse values from strings to attributes..
    val nEntries = values[0].size

    for (i in 0 until nEntries) {
        val id = try {
            values[0][i].toLong()
        } catch (e: NumberFormatException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "ExerciseTemplate: Failed to parse id")
            }
            throw SerializationException("File is corrupt.")
        }

        val name = values[1][i]
        val exerciseId = try {
            values[2][i].toLong()
        } catch (e: NumberFormatException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "ExerciseTemplate: Failed to parse exercise_id")
            }
            throw SerializationException("File is corrupt.")
        }

        val targetRepRange =  convertIntRangeFromString(values[3][i])
        if (targetRepRange == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "ExerciseTemplate: Failed to parse target_rep_range")
            }
            throw SerializationException("File is corrupt.")
        }

        res[id] = ExerciseTemplate(id, name, Exercise(exerciseId), targetRepRange)
    }

    return res
}


private fun parseSetTemplateItemSection(serialization: List<String>, loc: Pair<Int, Int>): List<SetTemplateWithItem> {
    val res = mutableListOf<SetTemplateWithItem>()

    val columns = listOf("set_template_id", "set_item_pos", "exercise_template_id", "rest_period_id")
    val values = extractColumns(serialization, loc, columns)

    // parse values from strings to attributes..
    val nEntries = values[0].size

    for (i in 0 until nEntries) {
        val setTemplateId = try {
            values[0][i].toLong()
        } catch (e: NumberFormatException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "SetTemplateItem: Failed to parse set_template_id")
            }
            throw SerializationException("File is corrupt.")
        }

        val setItemPos = try {
            values[1][i].toInt()
        } catch (e: NumberFormatException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "SetTemplateItem: Failed to parse set_item_pos")
            }
            throw SerializationException("File is corrupt.")
        }

        val exerciseTemplateId = try {
            values[2][i].toLong()
        } catch (e: NumberFormatException) {
            null
        }

        val restPeriodId = try {
            values[3][i].toLong()
        } catch (e: NumberFormatException) {
            null
        }

        if (exerciseTemplateId == null && restPeriodId == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "SetTemplateItem: Both exercise_template_id and rest_period_id are null")
            }
            throw SerializationException("File is corrupt.")
        }

        res.add(SetTemplateWithItem(setTemplateId, setItemPos, exerciseTemplateId, restPeriodId))
    }

    return res
}


private fun parseSetTemplateSection(serialization: List<String>, loc: Pair<Int, Int>): MutableMap<ItemIdentifier, SetTemplate> {
    val res = mutableMapOf<ItemIdentifier, SetTemplate>()

    val columns = listOf("id", "name")
    val values = extractColumns(serialization, loc, columns)

    // parse values from strings to attributes..
    val nEntries = values[0].size

    for (i in 0 until nEntries) {
        val id = try {
            values[0][i].toLong()
        } catch (e: NumberFormatException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "SetTemplate: Failed to parse id")
            }
            throw SerializationException("File is corrupt.")
        }

        val name = values[1][i]

        res[id] = SetTemplate(id, name)
    }

    return res
}


private fun parseWorkoutTemplateItemSection(serialization: List<String>, loc: Pair<Int, Int>): List<WorkoutTemplateWithItem> {
    val res = mutableListOf<WorkoutTemplateWithItem>()

    val columns = listOf("workout_template_id", "workout_item_pos", "set_template_id", "rest_period_id", "tags")
    val values = extractColumns(serialization, loc, columns)

    val nEntries = values[0].size

    for (i in 0 until nEntries) {
        val workoutTemplateId = try {
            values[0][i].toLong()
        } catch (e: NumberFormatException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "WorkoutTemplateItem: Failed to parse workout_template_id")
            }
            throw SerializationException("File is corrupt.")
        }

        val workoutItemPos = try {
            values[1][i].toInt()
        } catch (e: NumberFormatException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "WorkoutTemplateItem: Failed to parse workout_item_pos")
            }
            throw SerializationException("File is corrupt.")
        }

        val setTemplateId = try {
            values[2][i].toLong()
        } catch (e: NumberFormatException) {
            null
        }

        val restPeriodId = try {
            values[3][i].toLong()
        } catch (e: NumberFormatException) {
            null
        }

        if (setTemplateId == null && restPeriodId == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "WorkoutTemplateItem: Both set_template_id and rest_period_id are null")
            }
            throw SerializationException("File is corrupt.")
        }

        val tags = convertTagsFromString(values[4][i])?: mapOf()

        res.add(WorkoutTemplateWithItem(workoutTemplateId, workoutItemPos, setTemplateId, restPeriodId, tags))
    }

    return res
}


private fun parseWorkoutTemplateSection(serialization: List<String>, loc: Pair<Int, Int>): MutableMap<ItemIdentifier, WorkoutTemplate> {
    val res = mutableMapOf<ItemIdentifier, WorkoutTemplate>()

    val columns = listOf("id", "name")
    val values = extractColumns(serialization, loc, columns)

    val nEntries = values[0].size

    for (i in 0 until nEntries) {
        val id = try {
            values[0][i].toLong()
        } catch (e: NumberFormatException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "WorkoutTemplate: Failed to parse id")
            }
            throw SerializationException("File is corrupt.")
        }

        val name = values[1][i]

        res[id] = WorkoutTemplate(id, name)
    }

    return res
}


private fun parseProgramTemplateItemSection(serialization: List<String>, loc: Pair<Int, Int>): List<ProgramTemplateWithItem> {
    val res = mutableListOf<ProgramTemplateWithItem>()

    val columns = listOf("program_template_id", "program_item_pos", "workout_template_id", "rest_period_id")
    val values = extractColumns(serialization, loc, columns)

    // parse values from strings to attributes..
    val nEntries = values[0].size

    for (i in 0 until nEntries) {
        val programTemplateId = try {
            values[0][i].toLong()
        } catch (e: NumberFormatException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "ProgramTemplateItem: Failed to parse program_template_id")
            }
            throw SerializationException("File is corrupt.")
        }

        val programItemPos = try {
            values[1][i].toInt()
        } catch (e: NumberFormatException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "ProgramTemplateItem: Failed to parse program_item_pos")
            }
            throw SerializationException("File is corrupt.")
        }

        val workoutTemplateId = try {
            values[2][i].toLong()
        } catch (e: NumberFormatException) {
            null
        }

        val restPeriodId = try {
            values[3][i].toLong()
        } catch (e: NumberFormatException) {
            null
        }

        if (workoutTemplateId == null && restPeriodId == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "ProgramTemplateItem: Both workout_template_id and rest_period_id are null")
            }
            throw SerializationException("File is corrupt.")
        }

        res.add(ProgramTemplateWithItem(programTemplateId, programItemPos, workoutTemplateId, restPeriodId))
    }

    return res
}


private fun parseProgramTemplateSection(serialization: List<String>, loc: Pair<Int, Int>): ProgramTemplate {
    val columns = listOf("id", "name")
    val values = extractColumns(serialization, loc, columns)

    if (values.isEmpty()) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "ProgramTemplate: no entries")
        }
        throw Exception("File is corrupt.")
    }

    val id = try {
        values[0][0].toLong()
    } catch (e: NumberFormatException) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "ProgramTemplate: Failed to parse id")
        }
        throw SerializationException("File is corrupt.")
    }

    val name = values[1][0]

    return ProgramTemplate(id, name)
}


private fun extractColumns(serialization: List<String>, loc: Pair<Int, Int>, columns: List<String>): List<List<String>> {
    // read column names
    val headerColumnNames = serialization[loc.first].trim().split(COLUMN_DELIMITER)

    if (columns.size > headerColumnNames.size) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Not enough columns in section header")
        }
        throw SerializationException("File is corrupt.")
    }

    val columnPositions = mutableMapOf<String, Int>()
    columns.forEach { col ->
        val pos = headerColumnNames.indexOf(col)
        if (pos == -1) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Column not found: $col")
            }
            throw Exception("File is corrupt.")
        }

        columnPositions[col] = pos
    }

    val rows = extractRows(serialization, loc.first + 1, loc.second)
    val values: List<MutableList<String>> = List(columns.size) { mutableListOf() }

    for (r in rows) {
        val v = r.trim().split(COLUMN_DELIMITER)
        if (columns.size > v.size) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Not enough values for entry")
                Log.d(TAG, "columns: $columns")
                Log.d(TAG, "values: $v")
            }
            throw SerializationException("File is corrupt.")
        }

        columns.forEachIndexed { i, col ->
            values[i].add(v[columnPositions[col]!!])
        }
    }

    return values
}


private fun extractRows(serialization: List<String>, begin: Int, end: Int): List<String> {
    val rowStarts = mutableListOf<Int>()

    for (iLine in begin..end) {
        if (serialization[iLine].startsWith(ROW_DELIMITER)) {
            rowStarts.add(iLine)
        }
    }

    rowStarts.add(end + 1)

    val rows = mutableListOf<String>()

    for (i in 0 until rowStarts.size - 1) {
        if ((rowStarts[i+1] - rowStarts[i]) == 1) {
            // single line row
            rows.add(serialization[rowStarts[i]].trim().substring(ROW_DELIMITER.length))
        } else {
            // multiline row (we need to add back newline characters that got eaten by readLine())
            var rowSerialization = serialization[rowStarts[i]].trimStart().substring(ROW_DELIMITER.length) + "\n"
            for (iLine in (rowStarts[i] + 1) until rowStarts[i+1] - 1) {
                rowSerialization += serialization[iLine] + "\n"
            }

            rowSerialization += serialization[rowStarts[i+1] - 1].trimEnd()
            rows.add(rowSerialization)
        }
    }

    return rows
}


private fun isValidProgramTemplateItems(
    items: List<ProgramTemplateWithItem>,
    program: ProgramTemplate,
    workouts: Map<ItemIdentifier, WorkoutTemplate>,
    restPeriods: Map<ItemIdentifier, RestPeriod>): Boolean {

    val itemsWithPos = mutableMapOf<ItemIdentifier, MutableList<Int>>()
    itemsWithPos[program.id] = mutableListOf()

    items.forEach {
        if (it.programTemplateId != program.id) {
            return false
        }

        itemsWithPos[it.programTemplateId]!!.add(it.programItemPosition)

        if (it.workoutTemplateId != null && !workouts.containsKey(it.workoutTemplateId)) {
            return false
        }

        if (it.restPeriodId != null && !restPeriods.containsKey(it.restPeriodId)) {
            return false
        }
    }

    itemsWithPos.values.forEach {
        val sortedByPos = it.sorted()
        for (i in sortedByPos.indices) {
            if (sortedByPos[i] != i) {
                return false
            }
        }
    }

    return true
}


private fun isValidWorkoutTemplateItems(
    items: List<WorkoutTemplateWithItem>,
    workouts: Map<ItemIdentifier, WorkoutTemplate>,
    setTemplates: Map<ItemIdentifier, SetTemplate>,
    restPeriods: Map<ItemIdentifier, RestPeriod>): Boolean {

    val itemsWithPos = mutableMapOf<ItemIdentifier, MutableList<Int>>()
    workouts.keys.forEach {
        itemsWithPos[it] = mutableListOf()
    }

    items.forEach {
        if (!workouts.containsKey(it.workoutTemplateId)) {
            return false
        }

        itemsWithPos[it.workoutTemplateId]!!.add(it.workoutItemPosition)

        if (it.setTemplateId != null && !setTemplates.containsKey(it.setTemplateId)) {
            return false
        }

        if (it.restPeriodId != null && !restPeriods.containsKey(it.restPeriodId)) {
            return false
        }
    }

    itemsWithPos.values.forEach {
        val sortedByPos = it.sorted()
        for (i in sortedByPos.indices) {
            if (sortedByPos[i] != i) {
                return false
            }
        }
    }

    return true
}


private fun isValidSetTemplateItems(
    items: List<SetTemplateWithItem>,
    setTemplates: Map<ItemIdentifier, SetTemplate>,
    exerciseTemplates: Map<ItemIdentifier, ExerciseTemplate>,
    restPeriods: Map<ItemIdentifier, RestPeriod>): Boolean {

    val itemsWithPos = mutableMapOf<ItemIdentifier, MutableList<Int>>()
    setTemplates.keys.forEach {
        itemsWithPos[it] = mutableListOf()
    }

    items.forEach {
        if (!setTemplates.containsKey(it.setTemplateId)) {
            return false
        }

        itemsWithPos[it.setTemplateId]!!.add(it.setItemPosition)

        if (it.exerciseTemplateId != null && !exerciseTemplates.containsKey(it.exerciseTemplateId)) {
            return false
        }

        if (it.restPeriodId != null && !restPeriods.containsKey(it.restPeriodId)) {
            return false
        }
    }

    itemsWithPos.values.forEach {
        val sortedByPos = it.sorted()
        for (i in sortedByPos.indices) {
            if (sortedByPos[i] != i) {
                return false
            }
        }
    }

    return true
}


private fun isValidExerciseTemplates(
    exerciseTemplates: Map<ItemIdentifier, ExerciseTemplate>,
    exercises: Map<ItemIdentifier, Exercise>): Boolean {

    exerciseTemplates.forEach {
        if (!exercises.containsKey(it.value.exercise.id)) {
            return false
        }
    }

    return true
}


private suspend fun addRestPeriods(restPeriods: MutableMap<ItemIdentifier, RestPeriod>, repo: RestPeriodRepository): Int {
    var addedItems = 0

    restPeriods.keys.forEach {
        // check if repo already has entry with the same content
        val repoEntry = repo.hasRestPeriodWithSameContent(restPeriods[it]!!)
        if (repoEntry != null) {
            if(repoEntry.id != it) {
                restPeriods[it] = repoEntry
            }
            return@forEach
        }

        // add to repo
        val entry = restPeriods[it]!!
        val id = ItemIdentifierGenerator.generateId()
        restPeriods[it] = repo.addRestPeriod(id, entry.name, entry.targetRestPeriodSec)

        addedItems++
    }

    return addedItems
}


private suspend fun addExercises(exercises: MutableMap<ItemIdentifier, Exercise>, repo: ExerciseRepository): Int {
    var addedItems = 0

    exercises.keys.forEach {
        // check if repo already has entry with the same content
        val repoEntry = repo.hasExerciseWithSameContent(exercises[it]!!)
        if (repoEntry != null) {
            if(repoEntry.id != it) {
                exercises[it] = repoEntry
            }
            return@forEach
        }

        // add to repo
        val entry = exercises[it]!!
        val id = ItemIdentifierGenerator.generateId()

        exercises[it] = repo.addExercise(id, entry.name, entry.targetMuscle, entry.notes, entry.videoTutorial)

        addedItems++
    }

    return addedItems
}


private suspend fun addExerciseTemplates(
    exerciseTemplates: MutableMap<ItemIdentifier, ExerciseTemplate>,
    exercises: Map<ItemIdentifier, Exercise>,
    repo: ExerciseTemplateRepository): Int {

    var addedItems = 0

    exerciseTemplates.keys.forEach {
        val entry = exerciseTemplates[it]!!
        val entryFull = ExerciseTemplate(entry.id, entry.name, exercises[entry.exercise.id]!!, entry.targetRepRange)

        // check if repo already has entry with the same content
        val repoEntry = repo.hasExerciseTemplateWithSameContent(entryFull)
        if (repoEntry != null) {
            exerciseTemplates[it] = repoEntry
            return@forEach
        }

        // add to repo
        val id = ItemIdentifierGenerator.generateId()
        exerciseTemplates[it] = repo.addExerciseTemplate(id, entryFull.name, entryFull.exercise, entryFull.targetRepRange)

        addedItems++
    }

    return addedItems
}


private suspend fun addSetTemplates(
    setTemplates: MutableMap<ItemIdentifier, SetTemplate>, setTemplateItems: List<SetTemplateWithItem>,
    exerciseTemplates: Map<ItemIdentifier, ExerciseTemplate>, restPeriods: Map<ItemIdentifier, RestPeriod>,
    repo: SetTemplateRepository): Int {

    var addedItems = 0

    setTemplates.keys.forEach { setId ->
        val entry = setTemplates[setId]!!
        val entryFull = SetTemplate(entry.id, entry.name)

        // get the items of this set (also need to be arranged by pos)
        val itemsWithPos = mutableMapOf<Int, Item>()
        setTemplateItems.forEach { setItem ->
            if (setItem.setTemplateId == setId) {
                val item = if (setItem.isWithExerciseTemplate()) {
                    exerciseTemplates[setItem.exerciseTemplateId]!!
                } else {
                    restPeriods[setItem.restPeriodId]!!
                }

                itemsWithPos[setItem.setItemPosition] = item
            }
        }

        val items = mutableListOf<Item>()

        var pos = 0
        while(itemsWithPos.isNotEmpty()) {
            val item = itemsWithPos[pos]!! // is tested by validate call before

            items.add(item)
            itemsWithPos.remove(pos)
            pos++
        }

        entryFull.replaceAllWith(items)

        // check if repo already has entry with the same content
        val repoEntry = repo.hasSetTemplateWithSameContent(entryFull)
        if (repoEntry != null) {
            setTemplates[setId] = repoEntry
            return@forEach
        }

        // add to repo
        val id = ItemIdentifierGenerator.generateId()
        setTemplates[setId] = repo.addSetTemplate(id, entryFull.name, entryFull.items)

        addedItems++
    }

    return addedItems
}


private suspend fun addWorkoutTemplates(
    workoutTemplates: MutableMap<ItemIdentifier, WorkoutTemplate>, workoutTemplateItems: List<WorkoutTemplateWithItem>,
    setTemplates: Map<ItemIdentifier, SetTemplate>, restPeriods: Map<ItemIdentifier, RestPeriod>,
    repo: WorkoutTemplateRepository): Int {

    var addedItems = 0

    workoutTemplates.keys.forEach { workoutId ->
        val entry = workoutTemplates[workoutId]!!
        val entryFull = WorkoutTemplate(entry.id, entry.name)

        // get the items of this set (also need to be arranged by pos)
        val itemsWithPos = mutableMapOf<Int, Item>()
        workoutTemplateItems.forEach { workoutItem ->
            if (workoutItem.workoutTemplateId == workoutId) {
                val item = if (workoutItem.isWithSetTemplate()) {
                    TaggedItem(setTemplates[workoutItem.setTemplateId]!!, workoutItem.tags)
                } else {
                    restPeriods[workoutItem.restPeriodId]!!
                }

                itemsWithPos[workoutItem.workoutItemPosition] = item
            }
        }

        val items = mutableListOf<Item>()

        var pos = 0
        while(itemsWithPos.isNotEmpty()) {
            val item = itemsWithPos[pos]!! // is tested by validate call before

            items.add(item)
            itemsWithPos.remove(pos)
            pos++
        }

        entryFull.replaceAllWith(items)

        // check if repo already has entry with the same content
        val repoEntry = repo.hasWorkoutTemplateWithSameContent(entryFull)
        if (repoEntry != null) {
            workoutTemplates[workoutId] = repoEntry
            return@forEach
        }

        // add to repo
        val id = ItemIdentifierGenerator.generateId()
        workoutTemplates[workoutId] = repo.addWorkoutTemplate(id, entryFull.name, entryFull.items)

        addedItems++
    }

    return addedItems
}


private suspend fun addProgramTemplate(
    programTemplate: ProgramTemplate, programTemplateItems: List<ProgramTemplateWithItem>,
    workoutTemplates: Map<ItemIdentifier, WorkoutTemplate>, restPeriods: Map<ItemIdentifier, RestPeriod>,
    repo: ProgramTemplateRepository): Pair<Boolean, ProgramTemplate> {

    val entryFull = ProgramTemplate(programTemplate.id, programTemplate.name)

    // get the items of this set (also need to be arranged by pos)
    val itemsWithPos = mutableMapOf<Int, Item>()
    programTemplateItems.forEach { programItem ->
        if (programItem.programTemplateId == programTemplate.id) {
            val item = if (programItem.isWithWorkoutTemplate()) {
                workoutTemplates[programItem.workoutTemplateId]!!
            } else {
                restPeriods[programItem.restPeriodId]!!
            }

            itemsWithPos[programItem.programItemPosition] = item
        }
    }

    val items = mutableListOf<Item>()

    var pos = 0
    while(itemsWithPos.isNotEmpty()) {
        val item = itemsWithPos[pos]!! // is tested by validate call before

        items.add(item)
        itemsWithPos.remove(pos)
        pos++
    }

    entryFull.replaceAllWith(items)

    // check if repo already has entry with the same content
    val repoEntry = repo.hasProgramTemplateWithSameContent(entryFull)
    if (repoEntry != null) {
        return Pair(false, repoEntry)
    }

    // add to repo
    val id = ItemIdentifierGenerator.generateId()
    return Pair(true, repo.addProgramTemplate(id, entryFull.name, entryFull.items))
}