package com.example.gymbud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.activity.viewModels
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.gymbud.model.WorkoutSessionState
import com.example.gymbud.ui.viewmodel.LiveSessionViewModel
import com.example.gymbud.ui.viewmodel.LiveSessionViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val liveSessionViewModel: LiveSessionViewModel by viewModels {
        LiveSessionViewModelFactory((application as BaseApplication).sessionRepository)
    }

    private var disableBackNavigation = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.app_bar))

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
}