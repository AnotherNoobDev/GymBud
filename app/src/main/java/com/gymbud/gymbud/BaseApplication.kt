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

    lateinit var database: GymBudRoomDatabase
    private set

    lateinit var exerciseRepository: ExerciseRepository
    private set

    lateinit var exerciseTemplateRepository: ExerciseTemplateRepository
    private set

    lateinit var restPeriodRepository: RestPeriodRepository
    private set

    lateinit var setTemplateRepository: SetTemplateRepository
    private set

    lateinit var workoutTemplateRepository: WorkoutTemplateRepository
    private set

    lateinit var programRepository: ProgramTemplateRepository
    private set

    lateinit var itemRepository: ItemRepository
    private set

    lateinit var sessionRepository: SessionsRepository
    private set


    override fun onCreate() {
        super.onCreate()

        // NOTE: order here matters!

        this.database = GymBudRoomDatabase.getDatabase(this)

        this.restPeriodRepository = RestPeriodRepository(database.restPeriodDao())
        this.exerciseRepository = ExerciseRepository(database.exerciseDao())

        this.exerciseTemplateRepository = ExerciseTemplateRepository(
            database.exerciseTemplateDao(),
            exerciseRepository
        )

        this.setTemplateRepository = SetTemplateRepository(
            database.setTemplateDao(), database.setTemplateWithItemDao(),
            exerciseTemplateRepository, restPeriodRepository
        )

        this.workoutTemplateRepository = WorkoutTemplateRepository(
            database.workoutTemplateDao(), database.workoutTemplateWithItemDao(),
            setTemplateRepository, restPeriodRepository
        )

        this.programRepository = ProgramTemplateRepository(
            database.programTemplateDao(), database.programTemplateWithItemDao(),
            workoutTemplateRepository, restPeriodRepository
        )

        this.itemRepository = ItemRepository(
            database,
            exerciseRepository,
            exerciseTemplateRepository,
            restPeriodRepository,
            setTemplateRepository,
            workoutTemplateRepository,
            programRepository
        )

        this.sessionRepository = SessionsRepository(
            database.exerciseSessionRecordDao(),
            database.workoutSessionRecordDao(),
            workoutTemplateRepository
        )
    }


    fun resetDbConnection() {
        GymBudRoomDatabase.reset()

        this.database = GymBudRoomDatabase.getDatabase(this)

        // reset Dao's on repositories
        this.restPeriodRepository.setDao(database.restPeriodDao())
        this.exerciseRepository.setDao(database.exerciseDao())
        this.exerciseTemplateRepository.setDao(database.exerciseTemplateDao())
        this.setTemplateRepository.setDao(database.setTemplateDao(), database.setTemplateWithItemDao())
        this.workoutTemplateRepository.setDao(database.workoutTemplateDao(), database.workoutTemplateWithItemDao())
        this.programRepository.setDao(database.programTemplateDao(), database.programTemplateWithItemDao())
        this.sessionRepository.setDao(database.exerciseSessionRecordDao(), database.workoutSessionRecordDao())
    }
}