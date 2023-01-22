package com.gymbud.gymbud

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gymbud.gymbud.databinding.LayoutLiveSessionOverviewBinding
import com.gymbud.gymbud.model.WorkoutSessionItemType
import com.gymbud.gymbud.model.WorkoutSessionState
import com.gymbud.gymbud.ui.live_session.LiveSessionOverviewRecyclerViewAdapter
import com.gymbud.gymbud.ui.viewmodel.*
import com.gymbud.gymbud.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var navBar: BottomNavigationView

    private lateinit var appBar: Toolbar
    private lateinit var workoutSessionTimer: TextView

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

    // workout session timer
    private var startTime: Long = 0
    private val timerIntervalMs: Long = 1000 // 1 second in this case
    private var timerHandler: Handler? = null
    private var timerStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                updateTimer()
            } finally {
                timerHandler!!.postDelayed(this, timerIntervalMs)
            }
        }
    }


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
        lifecycleScope.launchWhenCreated {
            (application as BaseApplication).appRepository.useDarkTheme.collect {
                if (it) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            appViewModel.appWorkflowState.collect {
                when(it) {
                    AppWorkflowState.FirstTime -> prepareForFirstTimeWorkflow()
                    AppWorkflowState.Normal -> prepareForNormalWorkflow()
                    else -> {}
                }
            }
        }

        handleIntent()
    }


    private fun handleIntent() {
        if (intent == null) {
            return
        }

        if (intent.action != Intent.ACTION_VIEW) {
            return
        }

        val uri = intent.data ?: return
        //Log.d("Intent", uri.toString())

        intent.action = "" // hack to mark intent as resolved (otherwise handling will be triggered again on activity relaunch..)

        val name = getFileName(uri)
        if (name == null) {
            showToast("Failed to interpret file.")
            return
        }

        if (name.endsWith(GYMBUD_PROGRAM_FILE_EXTENSION)) {
            openProgramImportIntentDialog(uri, name)
        } else if (name.endsWith(GYMBUD_BACKUP_FILE_EXTENSION)) {
            openBackupRestoreIntentDialog(uri, name)
        } else {
            if (BuildConfig.DEBUG) {
                Log.e("Intent", "Don't know how to handle file: $name")
            }
        }
    }


    private fun openProgramImportIntentDialog(contentUri: Uri, contentName: String) {
        val context = (supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).requireContext()

        MaterialAlertDialogBuilder(context)
            .setTitle("Confirm Program import...")
            .setMessage("You are about to import a program from:\n\n$contentName.\n\n\nContinue?")
            .setPositiveButton("Ok") { _, _ ->
                handleProgramImportIntent(contentUri)
            }
            .setNegativeButton("Cancel"){_,_ ->}
            .show()
    }


    private fun handleProgramImportIntent(contentUri: Uri) {
        val inputStream = contentResolver.openInputStream(contentUri)
        if (inputStream == null) {
            showToast("Failed to import program due to: Unable to read content.")
            if (BuildConfig.DEBUG) {
                Log.e("Serialization", "Failed to import program due to: Unable to read content.")
            }

            return
        }

        val content = readFileContent(inputStream)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val (wasImported, importedProgramName) = deserializeProgramTemplate(content, (application as BaseApplication))
                    if (wasImported) {
                        showToast("Program has been imported as: $importedProgramName")
                    } else {
                        showToast("Program already present on device. Importing is not necessary.")
                    }
                } catch (e: SerializationException) {
                    showToast("Failed to import program due to: ${e.message}")
                }
                catch (e: Exception) {
                    showToast("Failed to import program due to: File is corrupt.")
                    if (BuildConfig.DEBUG) {
                        Log.e("Serialization", e.stackTrace.toString())
                    }
                }
            }
        }
    }


    private fun openBackupRestoreIntentDialog(contentUri: Uri, contentName: String) {
        val context = (supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).requireContext()

        MaterialAlertDialogBuilder(context)
            .setTitle("Confirm Restore...")
            .setMessage("You are about to restore data from:\n\n$contentName\n\n\nCAUTION: This will overwrite existing data (templates, workout history, etc.) on the device and cannot be undone! \n\n Continue?")
            .setPositiveButton("Ok") { _, _ ->
                handleBackupRestoreIntent(contentUri)
            }
            .setNegativeButton("Cancel"){_,_ ->}
            .show()
    }


    private fun handleBackupRestoreIntent(contentUri: Uri) {
        val inputStream = contentResolver.openInputStream(contentUri)
        if (inputStream == null) {
            showToast("Failed to start data restore process: Unable to read file.")
            if (BuildConfig.DEBUG) {
                Log.e("Serialization", "Failed to start data restore process: Unable to read file.")
            }

            return
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    restoreFromBackup(application as BaseApplication, inputStream)
                    showToast("Data restore was successful!")
                } catch (e: SerializationException) {
                    showToast(e.message?: "Restore failed! Local data may have been corrupted!")
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        Log.e("DataRestore", e.stackTrace.toString())
                    }
                    showToast("Restore failed! Local data may have been corrupted!")
                }
            }
        }
    }


    private fun getFileName(uri: Uri): String? {
        var result: String? = null

        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)?: return ""
            cursor.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    result = it.getString(index)
                }
            }
        }

        if (result == null) {
            result = uri.path

            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }

        return result
    }


    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                applicationContext,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }


    private fun setupActionBar() {
        appBar = findViewById(R.id.app_bar)
        workoutSessionTimer = appBar.findViewById(R.id.workout_session_timer)
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
            navBar.menu.findItem(R.id.liveSessionEndFragment).apply {
                isVisible = true
                setOnMenuItemClickListener {
                    openLiveSessionEndDialog()
                    true
                }
            }

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
        // visibility
        when(state) {
            WorkoutSessionState.NotReady, WorkoutSessionState.Ready -> setupActionBarVisibility(true)
            else -> setupActionBarVisibility(false)
        }

        // timer
        when (state) {
            WorkoutSessionState.Started -> setupWorkoutSessionTimer(true)
            else -> setupWorkoutSessionTimer(false)
        }
    }


    private fun setupActionBarVisibility(visible: Boolean) {
        appBarMenuVisible = visible

        appBar.menu.forEach {
            it.isVisible = visible
        }
    }


    private fun setupWorkoutSessionTimer(enabled: Boolean) {
        if (enabled) {
            workoutSessionTimer.visibility = View.VISIBLE
            startTimer()
        } else {
            workoutSessionTimer.visibility = View.GONE
            stopTimer()
        }
    }


    private fun startTimer() {
        startTime = liveSessionViewModel.getStartTime()

        timerHandler = Handler(Looper.getMainLooper())
        timerStatusChecker.run()
    }


    private fun stopTimer() {
        timerHandler?.removeCallbacks(timerStatusChecker)
    }


    private fun updateTimer() {
        val elapsedTimeSec = (System.currentTimeMillis() - startTime) / 1000
        workoutSessionTimer.text = TimeFormatter.getFormattedTimeHHMMSS(elapsedTimeSec)
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

        if (workoutSessionState == WorkoutSessionState.Started) {
            navController.navigate(R.id.dashboardFragment)
        }

        // ensure LiveSession survives app close
        liveSessionViewModel.onInterrupt()
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

        val currentItem = liveSessionViewModel.getCurrentItemIndex()
        adapter.update(liveSessionViewModel.getItems(), currentItem, liveSessionViewModel.getProgressedToItemIndex())

        binding.resultsRecyclerView.adapter = adapter
        binding.resultsRecyclerView.scrollToPosition(max(0, currentItem - 5)) // show the current item somewhere in the "middle"

        dialog.show()
    }


    private fun openLiveSessionEndDialog() {
        val context = (supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).requireContext()

        MaterialAlertDialogBuilder(context)
            .setTitle("End Workout Session?")
            .setPositiveButton("Ok") { _, _ ->
                navController.navigate(R.id.liveSessionEndFragment)
            }
            .setNegativeButton("Cancel"){_,_ ->}
            .show()
    }
}