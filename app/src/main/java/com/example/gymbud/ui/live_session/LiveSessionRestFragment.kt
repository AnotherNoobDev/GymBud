package com.example.gymbud.ui.live_session

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.gymbud.BaseApplication
import com.example.gymbud.databinding.FragmentLiveSessionRestBinding
import com.example.gymbud.model.WorkoutSessionItem
import com.example.gymbud.model.WorkoutSessionItemType
import com.example.gymbud.ui.viewmodel.LiveSessionViewModel
import com.example.gymbud.ui.viewmodel.LiveSessionViewModelFactory
import com.example.gymbud.utility.TimeFormatter


class LiveSessionRestFragment : Fragment() {
    private val liveSessionViewModel: LiveSessionViewModel by activityViewModels {
        LiveSessionViewModelFactory((activity?.application as BaseApplication).sessionRepository)
    }

    private var _binding: FragmentLiveSessionRestBinding? = null
    private val binding get() = _binding!!

    private lateinit var targetRestPeriod: IntRange

    private val timerIntervalMs: Long = 1000 // 1 second in this case
    private var timerHandler: Handler? = null

    private var startTime: Long = 0

    private var timerStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                updateTimer()
            } finally {
                timerHandler!!.postDelayed(this, timerIntervalMs)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveSessionRestBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val restPeriodSession = liveSessionViewModel.getCurrentItem() as WorkoutSessionItem.RestPeriodSession
        targetRestPeriod = restPeriodSession.getTargetRestPeriod()

        binding.apply {
            restLabel.text = "Rest ${restPeriodSession.getTargetRestPeriodAsStr()}"

            if (liveSessionViewModel.hasNextItem()) {
                nextItemHint.text = liveSessionViewModel.getNextItemHint()
                continueBtn.setOnClickListener {
                    proceedWithSession()
                }
            } else {
                continueBtn.text = "Finish"
                continueBtn.setOnClickListener {
                    finishSession()
                }
            }
        }

        startTimer()
    }


    private fun startTimer() {
        startTime = System.currentTimeMillis()

        timerHandler = Handler(Looper.getMainLooper())
        timerStatusChecker.run()
    }


    private fun stopTimer() {
        timerHandler?.removeCallbacks(timerStatusChecker)
    }


    private fun updateTimer() {
        val elapsedTimeSec = (System.currentTimeMillis() - startTime) / 1000

        binding.apply {
            timerValue.text = TimeFormatter.getFormattedTimeMMSS(elapsedTimeSec)
            timerProgressIndicator.progress = minOf(((elapsedTimeSec * 1.0 / targetRestPeriod.last) * 100).toInt(), 100)
            timerProgressIndicator.indicatorColor[0] = when {
                elapsedTimeSec < targetRestPeriod.first -> Color.RED
                (targetRestPeriod.first <= elapsedTimeSec) && (elapsedTimeSec < targetRestPeriod.last) -> Color.YELLOW
                else -> Color.GREEN
            }
        }
    }


    private fun proceedWithSession() {
        val action = when (liveSessionViewModel.getNextItemType()) {
            WorkoutSessionItemType.Exercise ->
                LiveSessionRestFragmentDirections.actionLiveSessionRestFragmentToLiveSessionExerciseFragment()
            WorkoutSessionItemType.Rest ->
                LiveSessionRestFragmentDirections.actionLiveSessionRestFragmentSelf()
            else ->
                null
        }

        if (action != null) {
            liveSessionViewModel.proceed()
            findNavController().navigate(action)
        }
    }


    private fun finishSession() {
        val action = LiveSessionRestFragmentDirections.actionLiveSessionRestFragmentToLiveSessionEndFragment()
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
    }
}