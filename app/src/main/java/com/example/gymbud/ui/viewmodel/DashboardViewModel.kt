package com.example.gymbud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymbud.data.AppRepository
import com.example.gymbud.data.ItemIdentifierGenerator
import com.example.gymbud.data.ItemRepository
import com.example.gymbud.model.Item
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.ItemType
import com.example.gymbud.model.ProgramTemplate
import kotlinx.coroutines.flow.*


private const val NO_PROGRAM_NAME = "None"


data class ActiveProgramAndProgramDay(
    val programName: String ,
    val programDayPosInProgram: Long, // -1 if not bound to a program
    val programDay: Item? // Workout or Rest day
)


class DashboardViewModel(
    private val itemRepository: ItemRepository,
    private val appRepository: AppRepository
): ViewModel() {


    fun getActiveProgramAndProgramDay(): Flow<ActiveProgramAndProgramDay> = appRepository.activeProgramAndProgramDay.map { (programId, programDayIdOrPos) ->
        determineActiveProgramAndProgramDayFromStorage(programId, programDayIdOrPos)
    }


    private suspend fun determineActiveProgramAndProgramDayFromStorage(programId: ItemIdentifier, programDayIdOrPos: Long): ActiveProgramAndProgramDay {
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
                val (pos, day) =  determineProgramBoundedProgramDayFromStorage(program, programDayIdOrPos.toInt())
                programDay = day
                programDayPosInProgram = pos.toLong()
            }
        }

        return ActiveProgramAndProgramDay(program?.name ?: NO_PROGRAM_NAME, programDayPosInProgram, programDay)
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


    private fun determineProgramBoundedProgramDayFromStorage(program: ProgramTemplate, pos: Int): Pair<Int, Item> {
        return if (pos >= program.items.size) {
            Pair(0, program.get(0))
        } else {
            Pair(pos, program.get(pos))
        }
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