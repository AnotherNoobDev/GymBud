package com.example.gymbud

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.gymbud.databinding.LayoutLiveSessionOverviewBinding
import com.example.gymbud.model.WorkoutSessionItemType
import com.example.gymbud.model.WorkoutSessionState
import com.example.gymbud.ui.live_session.LiveSessionOverviewRecyclerViewAdapter
import com.example.gymbud.ui.viewmodel.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var navBar: BottomNavigationView

    private lateinit var appBar: Toolbar

    private val liveSessionViewModel: LiveSessionViewModel by viewModels {
        LiveSessionViewModelFactory(
            (application as BaseApplication).sessionRepository,
            (application as BaseApplication).appRepository
        )
    }

    private val appViewModel: AppViewModel by viewModels {
        AppViewModelFactory()
    }

    private var workoutSessionState: WorkoutSessionState = WorkoutSessionState.NotReady

    private var disableBackNavigation = false

    private var appBarMenuVisible = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // note: lock orientation because UI/UX are lacking when in landscape at the moment
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(R.layout.activity_main)

        setupActionBar()

        setupNavigationBar()

        // in general we are ok with the keyboard hiding parts of the screen
        // and don't want any adjustment (resizing)
        // Fragments that need adjustment will take care of it themselves
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        // theme
        lifecycleScope.launch {
            (application as BaseApplication).appRepository.useDarkTheme.collect {
                if (it) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        lifecycleScope.launch {
            appViewModel.appWorkflowState.collect {
                when(it) {
                    AppWorkflowState.FirstTime -> prepareForFirstTimeWorkflow()
                    AppWorkflowState.Normal -> prepareForNormalWorkflow()
                    else -> {}
                }
            }
        }
    }


    private fun setupActionBar() {
        appBar = findViewById(R.id.app_bar)
        setSupportActionBar(appBar)

        val str = SpannableStringBuilder("GYMBUD")

        val colorPrimaryVariant = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimaryVariant, colorPrimaryVariant, true)

        val colorSecondary = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, colorSecondary, true)

        str.setSpan(ForegroundColorSpan(colorPrimaryVariant.data), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        str.setSpan(ForegroundColorSpan(colorSecondary.data), 3, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        supportActionBar!!.title = str
    }


    private fun setupNavigationBar() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navBar = findViewById(R.id.bottom_navigation)
        navBar.setupWithNavController(navController)
    }


    private fun prepareForFirstTimeWorkflow() {
        navBar.visibility = View.GONE
        setupActionBarVisibility(false)
    }


    private suspend fun prepareForNormalWorkflow() {
        liveSessionViewModel.state.collect {
            onLiveSessionStateChanged(it)
        }
    }


    private fun onLiveSessionStateChanged(state: WorkoutSessionState) {
        workoutSessionState = state

        presentBottomNavigation(state)
        presentActionBar(state)
        adjustNavigationControls(state)
    }


    private fun presentBottomNavigation(state: WorkoutSessionState) {
        navBar.visibility = if (state == WorkoutSessionState.Finished) View.GONE else View.VISIBLE

        val isLiveSessionMenu = (state == WorkoutSessionState.Started)

        // hack because we can only have 5 items in bottom_navigation_menu.xml (although only 3 would be visible at any time..)
        // since two of the items are not actually navigation locations, we can get away with just changing appearance and setting a click listener that prevents navigation
        if (isLiveSessionMenu) {
            // first item -> Live Session Map(Overview)
            navBar.menu.findItem(R.id.templatesFragment).apply {
                icon = AppCompatResources.getDrawable(applicationContext, R.drawable.ic_live_session_map)
                title = getString(R.string.live_session_map)
                setOnMenuItemClickListener {
                    openLiveSessionOverviewDialog()
                    true // use true to prevent navigation, false otherwise
                }
            }

            // second item -> To Live Session Current Item
            navBar.menu.findItem(R.id.dashboardFragment).apply {
                icon = AppCompatResources.getDrawable(applicationContext, R.drawable.ic_live_session_current_item_24)
                title = getString(R.string.live_session_current_item)
                setOnMenuItemClickListener {
                    liveSessionViewModel.resume()
                    navigateToCurrentLiveSessionItem()
                    true
                }
            }

            // third item -> End Live Session
            navBar.menu.findItem(R.id.liveSessionEndFragment).isVisible = true
            navBar.menu.findItem(R.id.statsFragment).isVisible = false

            // HACK to clear checked state from all items (otherwise second item stays checked, but we don't want any item checked...)
            // TODO already tried using isChecked.. but it seems that at least one item must be checked at all times?
            navBar.menu.findItem(R.id.liveSessionDummy).isChecked = false
        } else {
            // first item -> Templates
            navBar.menu.findItem(R.id.templatesFragment).apply {
                icon = AppCompatResources.getDrawable(applicationContext, R.drawable.ic_templates_24)
                title = getString(R.string.templates)
                setOnMenuItemClickListener {
                    false
                }
            }

            // second item -> Dashboard
            navBar.menu.findItem(R.id.dashboardFragment).apply {
                icon = AppCompatResources.getDrawable(applicationContext, R.drawable.ic_dashboard_24)
                title = getString(R.string.dashboard)
                setOnMenuItemClickListener {
                    false
                }
            }

            //third item -> Stats
            navBar.menu.findItem(R.id.liveSessionEndFragment).isVisible = false
            navBar.menu.findItem(R.id.statsFragment).isVisible = true
        }
    }


    private fun presentActionBar(state: WorkoutSessionState) {
        when(state) {
            WorkoutSessionState.NotReady, WorkoutSessionState.Ready -> setupActionBarVisibility(true)
            else -> setupActionBarVisibility(false)
        }
    }


    private fun setupActionBarVisibility(visible: Boolean) {
        appBarMenuVisible = visible

        appBar.menu.forEach {
            it.isVisible = visible
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
        setupActionBarVisibility(appBarMenuVisible)

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
        liveSessionViewModel.onInterrupt()
    }


    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            liveSessionViewModel.sessionRestored.collect {
                if (it) {
                    Log.d("partial_workout_session", "navigate")
                    navigateToCurrentLiveSessionItem()
                }
            }
        }

        liveSessionViewModel.onRestore()
    }


    private fun navigateToCurrentLiveSessionItem() {
        // navigate to current workout item
        when (liveSessionViewModel.getCurrentItemType()) {
            WorkoutSessionItemType.Exercise ->
                navController.navigate(R.id.liveSessionExerciseFragment)
            WorkoutSessionItemType.Rest ->
                navController.navigate(R.id.liveSessionRestFragment)
        }
    }


    private fun openLiveSessionOverviewDialog() {
        // TODO optimize.. we probably don't need to create everything here everytime we open the dialog :)
        val context = (supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).requireContext()

        val binding = LayoutLiveSessionOverviewBinding.inflate(LayoutInflater.from(context), null, false)

        val dialog = MaterialAlertDialogBuilder(context)
            .setView(binding.root)
            .create()

        val adapter = LiveSessionOverviewRecyclerViewAdapter(context) {
            liveSessionViewModel.goToItem(it)
            dialog.dismiss()
            navigateToCurrentLiveSessionItem()
        }

        adapter.update(liveSessionViewModel.getItems(), liveSessionViewModel.getCurrentItemIndex(), liveSessionViewModel.getProgressedToItemIndex())

        binding.resultsRecyclerView.adapter = adapter

        dialog.show()
    }
}