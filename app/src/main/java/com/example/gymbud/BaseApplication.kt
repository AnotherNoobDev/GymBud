package com.example.gymbud

import android.app.Application
import com.example.gymbud.data.*

class BaseApplication: Application() {
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
        WorkoutTemplateRepository(setTemplateRepository)
    }

    val programRepository: ProgramTemplateRepository by lazy {
        ProgramTemplateRepository()
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
}