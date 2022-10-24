package com.gymbud.gymbud.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gymbud.gymbud.BaseApplication
import com.gymbud.gymbud.data.ItemIdentifierGenerator
import com.gymbud.gymbud.data.repository.QuotesRepository
import com.gymbud.gymbud.databinding.FragmentDashboardBinding
import com.gymbud.gymbud.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gymbud.gymbud.R
import com.gymbud.gymbud.ui.viewmodel.*
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

    private val liveSessionViewModel: LiveSessionViewModel by activityViewModels {
        val app = (activity?.application as BaseApplication)
        LiveSessionViewModelFactory(
            app.sessionRepository,
            app.appRepository
        )
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
            .setPositiveButton("Ok") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val newActiveProgram = programOptions!![checkedItem].first
                    dashboardViewModel.setActiveProgram(newActiveProgram)
                }
            }
            .setNegativeButton("Cancel") {_,_ ->
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
            .setTitle("Select Active Program Day")
            .setSingleChoiceItems(programDaysByName.toTypedArray(), checkedItem) { _, which ->
                checkedItem = which
            }
            .setPositiveButton("Ok") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val newActiveProgramDay = programDayOptions!![checkedItem].first
                    dashboardViewModel.setActiveProgramDay(newActiveProgramDay)
                }
            }
            .setNegativeButton("Cancel") {_,_ ->
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

        binding.discardWorkoutBtn.setOnClickListener {
        }

        //Log.d("partial_workout_session", "navigate")
        //navigateToCurrentLiveSessionItem()
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

            activeProgramDayEditBtn.isEnabled = true
            activeProgramEditBtn.isEnabled = true

            workoutButtons.visibility = View.GONE
            motivationalQuote.text = quotesRepository.getQuoteOfTheDay()
            motivationalQuote.visibility = View.VISIBLE
        }
    }


    private suspend fun presentForWorkoutDay(workout: WorkoutTemplate) {
        binding.apply {
            activeProgramDayValue.text = workout.name
            workoutButtons.visibility = View.VISIBLE
            motivationalQuote.visibility = View.GONE

            if (liveSessionViewModel.canContinueWorkout(workout)) {
                // can't change workout
                activeProgramDayEditBtn.isEnabled = false
                activeProgramEditBtn.isEnabled = false

                startWorkoutBtn.text = "Resume  Workout"
                startWorkoutBtn.setOnClickListener {
                    viewLifecycleOwner.lifecycleScope.launch {
                        liveSessionViewModel.restorePartialSession()
                        navigateToCurrentLiveSessionItem()
                    }

                }

                discardWorkoutBtn.visibility = View.VISIBLE
                discardWorkoutBtn.setOnClickListener {
                    openDiscardWorkoutDialog()
                }
            } else {
               presentForNewWorkoutSession()
            }
        }
    }


    private fun presentForNewWorkoutSession() {
        binding.apply {
            activeProgramDayEditBtn.isEnabled = true
            activeProgramEditBtn.isEnabled = true

            startWorkoutBtn.text = "  Start Workout  "
            startWorkoutBtn.setOnClickListener {
                val action = DashboardFragmentDirections.actionDashboardFragmentToLiveSessionStartFragment(activeProgramId, activeProgramDay!!.id)
                findNavController().navigate(action)
            }

            discardWorkoutBtn.visibility = View.GONE
            discardWorkoutBtn.setOnClickListener {}
        }
    }


    private fun navigateToCurrentLiveSessionItem() {
        // navigate to current workout item
        when (liveSessionViewModel.getCurrentItemType()) {
            WorkoutSessionItemType.Exercise ->
                findNavController().navigate(R.id.liveSessionExerciseFragment)
            WorkoutSessionItemType.Rest ->
                findNavController().navigate(R.id.liveSessionRestFragment)
        }
    }


    private fun openDiscardWorkoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Discard Workout Session?")
            .setPositiveButton("Ok") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    liveSessionViewModel.discardPartialSession()
                    presentForNewWorkoutSession()
                }
            }
            .setNegativeButton("Cancel") {_,_ ->
            }
            .show()
    }

}