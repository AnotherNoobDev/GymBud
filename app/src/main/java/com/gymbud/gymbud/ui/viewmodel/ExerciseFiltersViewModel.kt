package com.gymbud.gymbud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gymbud.gymbud.data.repository.ProgramTemplateRepository
import com.gymbud.gymbud.model.ItemIdentifier
import com.gymbud.gymbud.model.ProgramTemplate
import com.gymbud.gymbud.utility.TimeFormatter
import kotlinx.coroutines.flow.*
import java.util.*


data class ExerciseFilters(
    val programTemplateId: ItemIdentifier?,
    val periodStart: Long?,
    val periodEnd: Long?
)




class ExerciseFiltersViewModel(programTemplateRepository: ProgramTemplateRepository): ViewModel() {
    private var _filterForProgram = MutableStateFlow<ProgramTemplate?>(null)
    val filterForProgram: Flow<ProgramTemplate?> = _filterForProgram.asStateFlow()

    private var _filterForPeriodStart = MutableStateFlow<Date?>(null)
    //val filterForPeriodStart: Flow<Date?> = _filterForPeriodStart.asStateFlow()

    private var _filterForPeriodEnd = MutableStateFlow<Date?>(null)
    //val filterForPeriodEnd: Flow<Date?> = _filterForPeriodEnd.asStateFlow()

    val filterForPeriod: Flow<Pair<Date?, Date?>> = _filterForPeriodStart.combine(_filterForPeriodEnd) { start, end ->
        Pair(start, end)
    }

    val filterForProgramOptions = programTemplateRepository.programTemplates


    fun getFilters(): ExerciseFilters {
        return ExerciseFilters(
            _filterForProgram.value?.id,
            _filterForPeriodStart.value?.time,
            _filterForPeriodEnd.value?.time
        )
    }


    fun updateProgramFilter(programTemplate: ProgramTemplate?) {
        _filterForProgram.value = programTemplate
    }


    fun updatePeriodStartFilter(periodStart: Date?) {
        _filterForPeriodStart.value =  periodStart
    }


    fun updatePeriodEndFilter(periodEnd: Date?) {
        _filterForPeriodEnd.value =  periodEnd
    }


    fun clearAll() {
        updateProgramFilter(null)
        updatePeriodStartFilter(null)
        updatePeriodEndFilter(null)
    }


    fun getFilterForProgramAsText(): Flow<String> = _filterForProgram.map {
        it?.name ?: NO_PROGRAM_FILTER_TEXT
    }


    fun getFilterForPeriodAsText(): String {
        if (_filterForPeriodStart.value == null && _filterForPeriodEnd.value == null) {
            return NO_PERIOD_FILTER_TEXT
        }

        if (_filterForPeriodStart.value != null && _filterForPeriodEnd.value != null) {
            return TimeFormatter.getFormattedDateDDMMYYYY(_filterForPeriodStart.value!!) + " to " +
                    TimeFormatter.getFormattedDateDDMMYYYY(_filterForPeriodEnd.value!!)
        }

        return if (_filterForPeriodStart.value == null) {
            "until " + TimeFormatter.getFormattedDateDDMMYYYY(_filterForPeriodEnd.value!!)
        } else {
            "from " + TimeFormatter.getFormattedDateDDMMYYYY(_filterForPeriodStart.value!!)
        }
    }


    companion object {
        const val NO_PROGRAM_FILTER_TEXT = "any"
        const val NO_PERIOD_FILTER_TEXT = "any"
    }
}


class ExerciseFiltersViewModelFactory(private val programTemplateRepository: ProgramTemplateRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseFiltersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExerciseFiltersViewModel(programTemplateRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}