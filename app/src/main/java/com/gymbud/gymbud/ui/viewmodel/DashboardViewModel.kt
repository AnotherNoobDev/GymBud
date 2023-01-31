package com.gymbud.gymbud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gymbud.gymbud.data.ItemIdentifierGenerator
import com.gymbud.gymbud.data.repository.AppRepository
import com.gymbud.gymbud.data.repository.ItemRepository
import com.gymbud.gymbud.model.*
import com.gymbud.gymbud.utility.getDaysPast
import kotlinx.coroutines.flow.*


private const val NO_PROGRAM_NAME = "None"


data class ActiveProgramAndProgramDay(
    val programName: String ,
    val programId: ItemIdentifier,
    val programDayPosInProgram: Long, // -1 if not bound to a program
    val programDay: Item? // Workout or Rest day
)


class DashboardViewModel(
    private val itemRepository: ItemRepository,
    private val appRepository: AppRepository
): ViewModel() {

    suspend fun getActiveProgramAndProgramDay(): Flow<ActiveProgramAndProgramDay> {
        // check if we need to progress to next program day in program (due to time passing)
        val (programId, programDayIdOrPos, timestamp) = appRepository.activeProgramAndProgramDay.first()
        val programAndProgramDay = determineActiveProgramAndProgramDayFromStorage(programId, programDayIdOrPos, timestamp)
        if (programAndProgramDay.programDayPosInProgram >= 0 && programAndProgramDay.programDayPosInProgram != programDayIdOrPos) {
            appRepository.updateActiveProgramDay(programAndProgramDay.programDayPosInProgram)
        }

        return appRepository.activeProgramAndProgramDay.map { (programId, programDayIdOrPos, timestamp) ->
            determineActiveProgramAndProgramDayFromStorage(programId, programDayIdOrPos, timestamp)
        }
    }


    private suspend fun determineActiveProgramAndProgramDayFromStorage(programId: ItemIdentifier, programDayIdOrPos: Long, programDayTimestamp: Long): ActiveProgramAndProgramDay {
        val program = determineActiveProgramFromStorage(programId)

        val programDay: Item?
        val programDayPosInProgram: Long

        if (programDayIdOrPos == ItemIdentifierGenerator.NO_ID) {
            programDay = null
            programDayPosInProgram = -1
        } else {
            if (program == null) {
                programDay = determineUnboundedProgramDayFromStorage(programDayIdOrPos)
                programDayPosInProgram = -1
            } else {
                val (pos, day) =  determineProgramBoundedProgramDayFromStorage(program, programDayIdOrPos.toInt(), programDayTimestamp)
                programDay = day
                programDayPosInProgram = pos.toLong()
            }
        }

        return ActiveProgramAndProgramDay(program?.name ?: NO_PROGRAM_NAME, programId, programDayPosInProgram, programDay)
    }


    private suspend fun determineActiveProgramFromStorage(programId: ItemIdentifier): ProgramTemplate? {
        return  if (programId == ItemIdentifierGenerator.NO_ID) {
            null
        } else {
            val item = itemRepository.getItem(programId, ItemType.PROGRAM_TEMPLATE).first()
            if (item == null) {
                null
            } else {
                item as ProgramTemplate
            }
        }
    }


    private suspend fun determineUnboundedProgramDayFromStorage(id: ItemIdentifier): Item? {
        return if (id == ItemIdentifierGenerator.NO_ID) {
            null
        } else {
            itemRepository.getItem(id, ItemType.WORKOUT_TEMPLATE).first()
        }
    }


    private fun determineProgramBoundedProgramDayFromStorage(program: ProgramTemplate, pos: Int, programDayTimestamp: Long): Pair<Int, Item> {
        var daysPast = getDaysPast(System.currentTimeMillis(), programDayTimestamp)
        var upToDatePos = pos

        if (daysPast > 0) {
            upToDatePos++
            daysPast--
        }

        while (daysPast > 0) {
            if (upToDatePos >= program.items.size) {
                upToDatePos = 0
            }

            // we can move past Rest Days without user opening the app, but don't auto skip workouts
            if (program.get(upToDatePos) is WorkoutTemplate) {
                break
            }

            upToDatePos++
            daysPast--
        }

        if (upToDatePos >= program.items.size) {
            upToDatePos = 0
        }

        return Pair(upToDatePos, program.get(upToDatePos))
    }


    // pairs of displayable names and IDs associated with each name (to be fed into setActiveProgram)
    fun getActiveProgramOptions(): Flow<List<Pair<ItemIdentifier, String>>> =
        itemRepository.getItemsByType(ItemType.PROGRAM_TEMPLATE).map { programs ->
            listOf(ItemIdentifierGenerator.NO_ID to NO_PROGRAM_NAME) + programs.map { program -> program.id to program.name}
        }


    // pairs of displayable names and IDs or positions associated with each name (to be fed into setActiveProgramDay)
    fun getActiveProgramDayOptions(): Flow<List<Pair<Long, String>>> =
        appRepository.activeProgramId.combine(itemRepository.getItemsByType(ItemType.WORKOUT_TEMPLATE)) { programId, workouts ->
            val program = determineActiveProgramFromStorage(programId)
            program?.items?.mapIndexed { index, item -> index.toLong() to item.name }
                ?: workouts.map { it.id to it.name }
        }


    suspend fun setActiveProgram(programId: ItemIdentifier) {
        if (programId == ItemIdentifierGenerator.NO_ID) {
            appRepository.updateActiveProgramAndProgramDay(programId, ItemIdentifierGenerator.NO_ID)
        } else {
            appRepository.updateActiveProgramAndProgramDay(programId, 0)
        }

    }


    suspend fun setActiveProgramDay(programDayIdOrPos: Long) {
        appRepository.updateActiveProgramDay(programDayIdOrPos)
    }
}


class DashboardViewModelFactory(private val itemRepository: ItemRepository, private val appRepository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(itemRepository, appRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}