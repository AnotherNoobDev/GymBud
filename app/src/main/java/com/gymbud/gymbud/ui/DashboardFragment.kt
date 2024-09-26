package com.gymbud.gymbud.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gymbud.gymbud.BaseApplication
import com.gymbud.gymbud.R
import com.gymbud.gymbud.data.ItemIdentifierGenerator
import com.gymbud.gymbud.data.repository.QuotesRepository
import com.gymbud.gymbud.data.repository.SessionsRepository
import com.gymbud.gymbud.databinding.FragmentDashboardBinding
import com.gymbud.gymbud.model.Item
import com.gymbud.gymbud.model.ItemIdentifier
import com.gymbud.gymbud.model.RestPeriod
import com.gymbud.gymbud.model.WorkoutSessionItemType
import com.gymbud.gymbud.model.WorkoutTemplate
import com.gymbud.gymbud.ui.viewmodel.ActiveProgramAndProgramDay
import com.gymbud.gymbud.ui.viewmodel.DashboardViewModel
import com.gymbud.gymbud.ui.viewmodel.DashboardViewModelFactory
import com.gymbud.gymbud.ui.viewmodel.LiveSessionViewModel
import com.gymbud.gymbud.ui.viewmodel.LiveSessionViewModelFactory
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
    private lateinit var sessionRepository: SessionsRepository

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
                    if (checkedItem >= 0) {
                        val newActiveProgram = programOptions!![checkedItem].first
                        dashboardViewModel.setActiveProgram(newActiveProgram)
                    }
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
                    if (checkedItem >= 0) {
                        val newActiveProgramDay = programDayOptions!![checkedItem].first
                        dashboardViewModel.setActiveProgramDay(newActiveProgramDay)
                    }
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
        sessionRepository = app.sessionRepository

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

        clearProgramDayContentArea()

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


    private fun clearProgramDayContentArea() {
        binding.apply {
            motivationalQuote.visibility = View.GONE
            workoutButtons.visibility = View.GONE
            sessionCompletedLabel.visibility = View.GONE
        }
    }


    private suspend fun presentForNoSelectionOrRestDay(programDayText: String) {
        binding.apply {
            activeProgramDayValue.text = programDayText

            activeProgramDayEditBtn.isEnabled = true
            activeProgramEditBtn.isEnabled = true

            motivationalQuote.text = quotesRepository.getQuoteOfTheDay()
            motivationalQuote.visibility = View.VISIBLE
        }
    }


    private suspend fun presentForWorkoutDay(workout: WorkoutTemplate) {
        binding.apply {
            activeProgramDayValue.text = workout.name

            if (liveSessionViewModel.canContinueWorkout()) {
                presentForResumingWorkoutSession()
            } else {
                val ses = sessionRepository.getTodaySession()
                if (ses == null) {
                    presentForNewWorkoutSession()
                } else {
                    if (ses.workoutTemplateId == activeProgramDay?.id) {
                        presentForCompletedWorkoutSession()
                    } else {
                        val sesName = dashboardViewModel.getSessionWorkoutName(ses)
                        presentForNewWorkoutSession(ses.id, sesName)
                    }
                }
            }
        }
    }


    private fun presentForResumingWorkoutSession() {
        binding.apply {
            // can't change workout
            activeProgramDayEditBtn.isEnabled = false
            activeProgramEditBtn.isEnabled = false

            workoutButtons.visibility = View.VISIBLE

            startWorkoutBtn.text = "Resume  Workout"
            startWorkoutBtn.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val restored = liveSessionViewModel.restorePartialSession(activeProgramDay as WorkoutTemplate)
                    if (restored) {
                        navigateToCurrentLiveSessionItem()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to resume workout.. \nSomething went very wrong   x_x",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            discardWorkoutBtn.visibility = View.VISIBLE
            discardWorkoutBtn.setOnClickListener {
                openDiscardWorkoutDialog()
            }
        }
    }


    private fun presentForNewWorkoutSession(todaySesId: ItemIdentifier = ItemIdentifierGenerator.NO_ID, todaySesName:String = "") {
        binding.apply {
            activeProgramDayEditBtn.isEnabled = true
            activeProgramEditBtn.isEnabled = true

            workoutButtons.visibility = View.VISIBLE

            startWorkoutBtn.text = "  Start Workout  "
            startWorkoutBtn.setOnClickListener {
                if(todaySesId != ItemIdentifierGenerator.NO_ID) {
                    // if a session already exists for day, warn user that today's session will be deleted if a new workout is started
                    // delete today's session on workout start
                    openNewWorkoutSessionWarningDialog(todaySesId, todaySesName)
                } else {
                    val action = DashboardFragmentDirections.actionDashboardFragmentToLiveSessionStartFragment(activeProgramId, activeProgramDay!!.id)
                    findNavController().navigate(action)
                }
            }

            discardWorkoutBtn.visibility = View.GONE
            discardWorkoutBtn.setOnClickListener {}
        }
    }


    private fun presentForCompletedWorkoutSession() {
        binding.apply {
            activeProgramDayEditBtn.isEnabled = true
            activeProgramEditBtn.isEnabled = true

            sessionCompletedLabel.visibility = View.VISIBLE
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


    private fun openNewWorkoutSessionWarningDialog(sesId: ItemIdentifier, sesName: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove previously recorded Workout Session?")
            .setMessage("Previously recorded workout session $sesName will be removed if a new workout is started!\n\nContinue?")
            .setPositiveButton("Ok") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    sessionRepository.removeSession(sesId)

                    val action = DashboardFragmentDirections.actionDashboardFragmentToLiveSessionStartFragment(activeProgramId, activeProgramDay!!.id)
                    findNavController().navigate(action)
                }
            }
            .setNegativeButton("Cancel") {_,_ ->
            }
            .show()
    }

}