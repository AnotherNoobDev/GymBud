package com.example.gymbud.data.repository

import com.example.gymbud.data.datasource.database.ExerciseSessionRecordDao
import com.example.gymbud.data.datasource.database.WorkoutSessionRecordDao
import com.example.gymbud.model.ExerciseSessionRecord
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.WorkoutSession
import com.example.gymbud.model.WorkoutSessionRecord
import kotlinx.coroutines.flow.first


private const val TAG = "SessionsRepo"


class SessionsRepository(
    private val exerciseSessionRecordDao: ExerciseSessionRecordDao,
    private val workoutSessionRecordDao: WorkoutSessionRecordDao,
    private val workoutTemplateRepository: WorkoutTemplateRepository
) {

    suspend fun addExerciseSessionRecord(record: ExerciseSessionRecord) {
        exerciseSessionRecordDao.insert(record)
    }


    suspend fun addWorkoutSessionRecord(record: WorkoutSessionRecord) {
        workoutSessionRecordDao.insert(record)
    }


    suspend fun getPreviousWorkoutSession(workoutTemplateId: ItemIdentifier): WorkoutSession? {
        val prevSesRecord = workoutSessionRecordDao.getPreviousSession(workoutTemplateId)
            ?: return null

        val template = workoutTemplateRepository.retrieveWorkoutTemplate(prevSesRecord.workoutTemplateId).first()
            ?: return null

        val prevSesExerciseRecords = exerciseSessionRecordDao.getFromSession(prevSesRecord.id)
            ?: return null

        return WorkoutSession.fromRecord(prevSesRecord, template, prevSesExerciseRecords)
    }
}