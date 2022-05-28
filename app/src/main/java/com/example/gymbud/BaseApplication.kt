package com.example.gymbud

import android.app.Application
import com.example.gymbud.data.*

class BaseApplication: Application() {
    val exerciseRepository: ExerciseRepository = ExerciseRepository()
    val exerciseTemplateRepository: ExerciseTemplateRepository = ExerciseTemplateRepository(exerciseRepository)
    val restPeriodRepository: RestPeriodRepository = RestPeriodRepository()
    val setTemplateRepository: SetTemplateRepository = SetTemplateRepository(exerciseTemplateRepository)
    val workoutTemplateRepository: WorkoutTemplateRepository = WorkoutTemplateRepository(setTemplateRepository)
    val programRepository: ProgramRepository = ProgramRepository()
    val itemRepository: ItemRepository =
        ItemRepository(
            exerciseRepository,
            exerciseTemplateRepository,
            restPeriodRepository,
            setTemplateRepository,
            workoutTemplateRepository,
            programRepository
        )
}