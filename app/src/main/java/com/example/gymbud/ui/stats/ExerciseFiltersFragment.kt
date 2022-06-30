package com.example.gymbud.ui.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.gymbud.BaseApplication
import com.example.gymbud.R
import com.example.gymbud.databinding.FragmentExerciseFiltersBinding
import com.example.gymbud.model.ProgramTemplate
import com.example.gymbud.ui.viewmodel.ExerciseFiltersViewModel
import com.example.gymbud.ui.viewmodel.ExerciseFiltersViewModelFactory
import com.example.gymbud.utility.TimeFormatter
import com.example.gymbud.utility.addDays
import com.google.android.material.datepicker.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


class ExerciseFiltersFragment : Fragment() {
    private val exerciseFiltersViewModel: ExerciseFiltersViewModel by activityViewModels {
        ExerciseFiltersViewModelFactory((activity?.application as BaseApplication).programRepository)
    }

    private var _binding: FragmentExerciseFiltersBinding? = null
    private val binding get() = _binding!!

    private lateinit var startDatePicker: MaterialDatePicker<Long>
    private lateinit var endDatePicker: MaterialDatePicker<Long>

    private var programTemplateOptions: List<ProgramTemplate>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            programInput.onItemClickListener =
                OnItemClickListener { _, _, p2, _ ->
                    if (programTemplateOptions != null) {
                        exerciseFiltersViewModel.updateProgramFilter(programTemplateOptions!![p2])
                    }
                }

            startDateInput.isFocusable = false
            startDateInput.setOnClickListener {
                startDatePicker.show(parentFragmentManager, "START_DATE_PICKER")
            }

            endDateInput.isFocusable = false
            endDateInput.setOnClickListener {
                endDatePicker.show(parentFragmentManager, "END_DATE_PICKER")
            }

            programClearBtn.setOnClickListener {
                exerciseFiltersViewModel.updateProgramFilter(null)
            }

            startDateClearBtn.setOnClickListener {
                exerciseFiltersViewModel.updatePeriodStartFilter(null)
            }

            endDateClearBtn.setOnClickListener {
                exerciseFiltersViewModel.updatePeriodEndFilter(null)
            }

            clearAllBtn.setOnClickListener {
                exerciseFiltersViewModel.clearAll()
            }
        }

        listenToProgramFilter()
        listenToPeriodFilter()
    }


    private fun createStartDatePicker(selection: Long?, beforeConstraint: Long?) {
        val constraints = if (beforeConstraint != null) {
            val oneDayBeforeConstrain = addDays(beforeConstraint, -1)
            CalendarConstraints.Builder()
                .setEnd(oneDayBeforeConstrain)
                .setValidator(DateValidatorPointBackward.before(oneDayBeforeConstrain))
                .build()
        } else {
            null
        }

        startDatePicker = MaterialDatePicker
            .Builder.datePicker()
            .setSelection(selection)
            .setCalendarConstraints(constraints)
            .setPositiveButtonText("OK")
            .setTitleText("From..")
            .build()

        startDatePicker.addOnPositiveButtonClickListener {
            exerciseFiltersViewModel.updatePeriodStartFilter(Date(it))
        }
    }


    private fun createEndDatePicker(selection: Long?, afterConstraint: Long?) {
        val constraints = if (afterConstraint != null) {
            val oneDayAfterConstrain = addDays(afterConstraint, 1)
            CalendarConstraints.Builder()
                .setStart(oneDayAfterConstrain)
                .setValidator(DateValidatorPointForward.from(oneDayAfterConstrain))
                .build()
        } else {
            null
        }

        endDatePicker = MaterialDatePicker
            .Builder.datePicker()
            .setSelection(selection)
            .setCalendarConstraints(constraints)
            .setPositiveButtonText("OK")
            .setTitleText("..Until")
            .build()

        endDatePicker.addOnPositiveButtonClickListener {
            exerciseFiltersViewModel.updatePeriodEndFilter(Date(it))
        }
    }


    private fun listenToProgramFilter() {
        viewLifecycleOwner.lifecycleScope.launch {
            exerciseFiltersViewModel.filterForProgram.collect {
                if (it == null) {
                    binding.programInput.setText(ExerciseFiltersViewModel.NO_PROGRAM_FILTER_TEXT)
                    binding.programClearBtn.isEnabled = false
                } else {
                    binding.programInput.setText(it.name)
                    binding.programClearBtn.isEnabled = true
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            exerciseFiltersViewModel.filterForProgramOptions.collect {
                programTemplateOptions = it
                val programsByName = it.map { program -> program.name }

                val programOptionsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list_item, programsByName)
                binding.programInput.setAdapter(programOptionsAdapter)
            }
        }
    }


    private fun listenToPeriodFilter() {
        viewLifecycleOwner.lifecycleScope.launch {
            exerciseFiltersViewModel.filterForPeriod.collect { (startDate, endDate) ->
                if (startDate == null) {
                    binding.startDateInput.setText(ExerciseFiltersViewModel.NO_PERIOD_FILTER_TEXT)
                    binding.startDateClearBtn.isEnabled = false
                } else {
                    binding.startDateInput.setText(TimeFormatter.getFormattedDateDDMMYYYY(startDate))
                    binding.startDateClearBtn.isEnabled = true
                }

                createStartDatePicker(startDate?.time, endDate?.time)

                if (endDate == null) {
                    binding.endDateInput.setText(ExerciseFiltersViewModel.NO_PERIOD_FILTER_TEXT)
                    binding.endDateClearBtn.isEnabled = false
                } else {
                    binding.endDateInput.setText(TimeFormatter.getFormattedDateDDMMYYYY(endDate))
                    binding.endDateClearBtn.isEnabled = true
                }

                createEndDatePicker(endDate?.time, startDate?.time)
            }
        }
    }
}