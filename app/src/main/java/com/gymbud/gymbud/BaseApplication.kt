package com.gymbud.gymbud

import android.app.Application
import com.gymbud.gymbud.data.ItemIdentifierGenerator
import com.gymbud.gymbud.data.datasource.database.GymBudRoomDatabase
import com.gymbud.gymbud.data.repository.*

class BaseApplication: Application() {

    init {
        ItemIdentifierGenerator.setApp(this)
    }

    val appRepository: AppRepository by lazy {
        AppRepository(this)
    }

    val quotesRepository: QuotesRepository by lazy {
        QuotesRepository(this)
    }

    val database: GymBudRoomDatabase by lazy {
        GymBudRoomDatabase.getDatabase(this)
    }

    val exerciseRepository: ExerciseRepository by lazy {
        ExerciseRepository(database.exerciseDao())
    }

    val exerciseTemplateRepository: ExerciseTemplateRepository by lazy {
        ExerciseTemplateRepository(database.exerciseTemplateDao(), exerciseRepository)
    }

    val restPeriodRepository: RestPeriodRepository by lazy {
        RestPeriodRepository(database.restPeriodDao())
    }

    val setTemplateRepository: SetTemplateRepository by lazy {
        SetTemplateRepository(
            database.setTemplateDao(), database.setTemplateWithItemDao(),
            exerciseTemplateRepository, restPeriodRepository
        )
    }

    val workoutTemplateRepository: WorkoutTemplateRepository by lazy {
        WorkoutTemplateRepository(
            database.workoutTemplateDao(), database.workoutTemplateWithItemDao(),
            setTemplateRepository, restPeriodRepository
        )
    }

    val programRepository: ProgramTemplateRepository by lazy {
        ProgramTemplateRepository(
            database.programTemplateDao(), database.programTemplateWithItemDao(),
            workoutTemplateRepository, restPeriodRepository
        )
    }

    val itemRepository: ItemRepository by lazy {
        ItemRepository(
            database,
            exerciseRepository,
            exerciseTemplateRepository,
            restPeriodRepository,
            setTemplateRepository,
            workoutTemplateRepository,
            programRepository
        )
    }

    val sessionRepository: SessionsRepository by lazy {
        SessionsRepository(
            database.exerciseSessionRecordDao(),
            database.workoutSessionRecordDao(),
            workoutTemplateRepository
        )
    }
}