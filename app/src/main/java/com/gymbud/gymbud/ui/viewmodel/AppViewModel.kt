package com.gymbud.gymbud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

enum class AppWorkflowState {
    Startup,
    FirstTime,
    Normal
}


class AppViewModel: ViewModel() {
    private val _appWorkflowState: MutableStateFlow<AppWorkflowState> = MutableStateFlow(AppWorkflowState.Startup)
    val appWorkflowState: Flow<AppWorkflowState> = _appWorkflowState

    fun setAppWorkflowState(state: AppWorkflowState) {
        if (_appWorkflowState.value == AppWorkflowState.Normal) {
            // can't go back to FirstTimeStartup after we entered Normal workflow
            if (state == AppWorkflowState.FirstTime) {
                return
            }
        }

        _appWorkflowState.value = state
    }
}


class AppViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}