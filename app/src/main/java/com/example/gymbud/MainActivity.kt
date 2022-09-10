package com.example.gymbud

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.gymbud.data.ItemIdentifierGenerator
import com.example.gymbud.model.PartialWorkoutSessionRecord
import com.example.gymbud.model.WorkoutSessionItemType
import com.example.gymbud.model.WorkoutSessionState
import com.example.gymbud.ui.viewmodel.LiveSessionViewModel
import com.example.gymbud.ui.viewmodel.LiveSessionViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


private const val PARTIAL_WORKOUT_SESSION_TAG = "partial_workout_session"

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val liveSessionViewModel: LiveSessionViewModel by viewModels {
        LiveSessionViewModelFactory((application as BaseApplication).sessionRepository)
    }

    private var workoutSessionState: WorkoutSessionState = WorkoutSessionState.NotReady

    private var disableBackNavigation = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // note: lock orientation because UI/UX are lacking when in landscape at the moment
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.app_bar))

        val str = SpannableStringBuilder("GYMBUD")

        val colorPrimaryVariant = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimaryVariant, colorPrimaryVariant, true)

        val colorSecondary = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, colorSecondary, true)

        str.setSpan(ForegroundColorSpan(colorPrimaryVariant.data), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        str.setSpan(ForegroundColorSpan(colorSecondary.data), 3, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        supportActionBar!!.title = str

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val navBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navBar.setupWithNavController(navController)

        lifecycleScope.launch {
            liveSessionViewModel.state.collect {
                onLiveSessionStateChanged(it)
            }
        }
    }


    private fun onLiveSessionStateChanged(state: WorkoutSessionState) {
        workoutSessionState = state

        presentBottomNavigation(state)
        presentActionBar(state)
        adjustNavigationControls(state)
    }


    private fun presentBottomNavigation(state: WorkoutSessionState) {
        val navBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navBar.visibility = View.VISIBLE

        when(state) {
            WorkoutSessionState.NotReady, WorkoutSessionState.Ready -> {
                navBar.menu.forEach {
                    it.isVisible = true
                }

                navBar.menu.findItem(R.id.liveSessionEndFragment).isVisible = false
            }
            WorkoutSessionState.Started  -> {
                navBar.menu.forEach {
                    it.isVisible = false
                }

                navBar.menu.findItem(R.id.liveSessionEndFragment).isVisible = true
            }
            WorkoutSessionState.Finished -> {
                navBar.visibility = View.GONE
            }
        }
    }


    private fun presentActionBar(state: WorkoutSessionState) {
        val appBar = findViewById<Toolbar>(R.id.app_bar)

        when(state) {
            WorkoutSessionState.NotReady, WorkoutSessionState.Ready -> appBar.menu.forEach {
                it.isVisible = true
            }
            else -> appBar.menu.forEach {
                it.isVisible = false
            }
        }
    }


    private fun adjustNavigationControls(state: WorkoutSessionState) {
        disableBackNavigation = when (state) {
            WorkoutSessionState.NotReady, WorkoutSessionState.Ready -> false
            else -> true
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            navController.navigate(R.id.settingsFragment)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    override fun onBackPressed() {
        if (!disableBackNavigation) {
            super.onBackPressed()
        }
    }


    override fun onPause() {
        super.onPause()

        // ensure LiveSession survives app close
        if (workoutSessionState == WorkoutSessionState.Started) {
            lifecycleScope.launch {
                Log.d(PARTIAL_WORKOUT_SESSION_TAG,"Saving session")
                // persist partial workout session
                val atItem = liveSessionViewModel.getCurrentItemIndex()
                val restTimerStartTime = liveSessionViewModel.getRestTimerStartTime()

                liveSessionViewModel.finish()
                val workoutSessionId = liveSessionViewModel.saveSession("")

                // update app repository with partial workout session id
                (application as BaseApplication).appRepository.savePartialWorkoutSessionInfo(
                    PartialWorkoutSessionRecord(workoutSessionId, atItem, restTimerStartTime)
                )

                Log.d(PARTIAL_WORKOUT_SESSION_TAG,"Session saved")
            }
        }
    }


    override fun onResume() {
        super.onResume()

        // restore LiveSession
        lifecycleScope.launch {
            // we are done if no partialWorkoutSession was persisted
            val partialWorkoutSession = (application as BaseApplication).appRepository.partialWorkoutSessionRecord.first()

            Log.d(PARTIAL_WORKOUT_SESSION_TAG, "onResume: $partialWorkoutSession")

            if (partialWorkoutSession.workoutSessionId == ItemIdentifierGenerator.NO_ID) {
                return@launch
            }

            lifecycleScope.launch {
                liveSessionViewModel.restore(partialWorkoutSession)
                (application as BaseApplication).appRepository.clearPartialWorkoutSessionInfo()

                // navigate to current workout item
                when (liveSessionViewModel.getCurrentItemType()) {
                    WorkoutSessionItemType.Exercise ->
                        navController.navigate(R.id.liveSessionExerciseFragment)
                    WorkoutSessionItemType.Rest ->
                        navController.navigate(R.id.liveSessionRestFragment)
                }
            }
        }
    }
}