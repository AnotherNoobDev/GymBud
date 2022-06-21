package com.example.gymbud.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gymbud.BaseApplication
import com.example.gymbud.data.ItemIdentifierGenerator
import com.example.gymbud.data.repository.QuotesRepository
import com.example.gymbud.databinding.FragmentDashboardBinding
import com.example.gymbud.model.*
import com.example.gymbud.ui.viewmodel.ActiveProgramAndProgramDay
import com.example.gymbud.ui.viewmodel.DashboardViewModel
import com.example.gymbud.ui.viewmodel.DashboardViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


//private const val TAG = "DashboardFragment"
private const val NO_PROGRAM_DAY_NAME = "Not Selected"


class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val dashboardViewModel: DashboardViewModel by activityViewModels {
        val app = (activity?.application as BaseApplication)
        DashboardViewModelFactory(app.itemRepository, app.appRepository)
    }

    private lateinit var quotesRepository: QuotesRepository

    private var activeProgram: String = ""
    private var activeProgramId: ItemIdentifier = ItemIdentifierGenerator.NO_ID
    private var activeProgramDay: Item? = null
    private var activeProgramDayPosInProgram: Long = -1

    private var programOptions: List<Pair<ItemIdentifier, String>>? = null
    private var programDayOptions: List<Pair<Long, String>>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        binding.apply {
            activeProgramEditBtn.setOnClickListener {
                onEditActiveProgram()
            }

            activeProgramDayEditBtn.setOnClickListener {
                onEditActiveProgramDay()
            }

            startWorkoutBtn.setOnClickListener {
                val action = DashboardFragmentDirections.actionDashboardFragmentToLiveSessionStartFragment(activeProgramId, activeProgramDay!!.id)
                findNavController().navigate(action)
            }
        }

        return binding.root
    }


    private fun onEditActiveProgram() {
        val programsByName = programOptions?.map { (_, name) -> name }?: listOf()
        var checkedItem = programsByName.indexOf(activeProgram)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Active Program")
            .setSingleChoiceItems(programsByName.toTypedArray(), checkedItem) { _, which ->
                checkedItem = which
            }
            .setPositiveButton("Confirm") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val newActiveProgram = programOptions!![checkedItem].first
                    dashboardViewModel.setActiveProgram(newActiveProgram)
                }
            }
            .setNegativeButton("Dismiss") {_,_ ->
            }
            .show()
    }


    private fun onEditActiveProgramDay() {
        val programDaysByName = programDayOptions?.map {(_,name) -> name} ?: listOf()
        var checkedItem = activeProgramDayPosInProgram.toInt()
        if (checkedItem == -1) {
            checkedItem = programDaysByName.indexOf(activeProgramDay?.name)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Active Program")
            .setSingleChoiceItems(programDaysByName.toTypedArray(), checkedItem) { _, which ->
                checkedItem = which
            }
            .setPositiveButton("Confirm") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val newActiveProgramDay = programDayOptions!![checkedItem].first
                    dashboardViewModel.setActiveProgramDay(newActiveProgramDay)
                }
            }
            .setNegativeButton("Dismiss") {_,_ ->
            }
            .show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = activity?.application as BaseApplication
        quotesRepository = app.quotesRepository

        viewLifecycleOwner.lifecycleScope.launch {
            dashboardViewModel.getActiveProgramAndProgramDay().collect {
                updateActiveProgramAndProgramDay(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            dashboardViewModel.getActiveProgramOptions().collect {
                programOptions = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            dashboardViewModel.getActiveProgramDayOptions().collect {
                programDayOptions = it
            }
        }
    }


    private suspend fun updateActiveProgramAndProgramDay(active : ActiveProgramAndProgramDay) {
        updateProgram(active.programName)
        activeProgramId = active.programId

        updateProgramDay(active.programDay)
        activeProgramDayPosInProgram = active.programDayPosInProgram

    }


    private fun updateProgram(program: String) {
        activeProgram = program
        binding.activeProgramValue.text = activeProgram
    }


    private suspend fun updateProgramDay(programDay: Item?) {
        activeProgramDay = programDay

        if (activeProgramDay == null) {
            presentForNoSelectionOrRestDay(NO_PROGRAM_DAY_NAME)
            return
        }

        if (activeProgramDay is RestPeriod) {
            presentForNoSelectionOrRestDay(activeProgramDay!!.name)
            return
        }

        presentForWorkoutDay(activeProgramDay as WorkoutTemplate)
    }


    private suspend fun presentForNoSelectionOrRestDay(programDayText: String) {
        binding.apply {
            activeProgramDayValue.text = programDayText
            startWorkoutBtn.visibility = View.GONE
            motivationalQuote.text = quotesRepository.getQuoteOfTheDay()
            motivationalQuote.visibility = View.VISIBLE
        }
    }


    private fun presentForWorkoutDay(workout: WorkoutTemplate) {
        binding.apply {
            activeProgramDayValue.text = workout.name
            startWorkoutBtn.visibility = View.VISIBLE
            motivationalQuote.visibility = View.GONE
        }
    }
}