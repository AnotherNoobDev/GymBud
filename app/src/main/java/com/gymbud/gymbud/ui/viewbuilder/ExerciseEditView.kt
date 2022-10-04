package com.gymbud.gymbud.ui.viewbuilder

import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import com.gymbud.gymbud.R
import com.gymbud.gymbud.databinding.LayoutDetailNameBinding
import com.gymbud.gymbud.databinding.LayoutEditDropdownFieldBinding
import com.gymbud.gymbud.databinding.LayoutEditTextFieldBinding
import com.gymbud.gymbud.model.*
import com.gymbud.gymbud.ui.viewmodel.ItemViewModel
import com.gymbud.gymbud.utility.YoutubeHelper


private const val TAG = "ExerciseEV"


class ExerciseEditView(
    private val context: Context
): EditItemView {
    private var _titleBinding: LayoutDetailNameBinding? = null
    private val titleBinding get() = _titleBinding!!

    private var _nameBinding: LayoutEditTextFieldBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _targetMuscleBinding: LayoutEditDropdownFieldBinding? = null
    private val targetMuscleBinding get() = _targetMuscleBinding!!

    private var _notesBinding: LayoutEditTextFieldBinding? = null
    private val notesBinding get() = _notesBinding!!

    private var _videoTutorialBinding: LayoutEditTextFieldBinding? = null
    private val videoTutorialBinding get() = _videoTutorialBinding!!


    override fun inflate(inflater: LayoutInflater): List<View> {
        _titleBinding = LayoutDetailNameBinding.inflate(inflater)

        _nameBinding = LayoutEditTextFieldBinding.inflate(inflater)
        nameBinding.label.hint = context.getString(R.string.item_name)
        nameBinding.input.setOnClickListener {
            nameBinding.label.error = null
        }

        _targetMuscleBinding = LayoutEditDropdownFieldBinding.inflate(inflater)
        targetMuscleBinding.label.setStartIconDrawable(R.drawable.ic_target_muscle_24)
        targetMuscleBinding.label.hint = context.getString(R.string.target_muscle)

        val muscleGroups = MuscleGroup.values()
        val targetMuscleAdapter = ArrayAdapter(context, R.layout.dropdown_list_item, muscleGroups)
        targetMuscleBinding.input.setAdapter(targetMuscleAdapter)

        _notesBinding = LayoutEditTextFieldBinding.inflate(inflater)
        notesBinding.label.setStartIconDrawable(R.drawable.ic_notes_24)
        notesBinding.label.hint = context.getString(R.string.item_notes)
        notesBinding.input.inputType = InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_FLAG_MULTI_LINE)

        _videoTutorialBinding = LayoutEditTextFieldBinding.inflate(inflater)
        videoTutorialBinding.label.setStartIconDrawable(R.drawable.ic_video_player_24)
        videoTutorialBinding.label.hint = context.getString(R.string.item_video_tutorial)
        videoTutorialBinding.input.setOnClickListener {
            videoTutorialBinding.label.error = null
        }

        return listOf(
            titleBinding.root,
            nameBinding.root,
            targetMuscleBinding.root,
            notesBinding.root,
            videoTutorialBinding.root
        )
    }


    override fun performTransactions(fragmentManager: FragmentManager) {
    }


    override fun populate(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel,
        item: Item
    ) {
        if (item !is Exercise) {
            Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not an exercise!")
            return
        }

        titleBinding.name.text="Modify Exercise"
        nameBinding.input.setText(item.name,  TextView.BufferType.SPANNABLE)
        targetMuscleBinding.input.setText(item.targetMuscle.toString(), false)
        notesBinding.input.setText(item.notes, TextView.BufferType.SPANNABLE)
        videoTutorialBinding.input.setText(YoutubeHelper.videoIdToURL(item.videoTutorial),TextView.BufferType.SPANNABLE)
    }


    override fun populateForNewItem(lifecycle: LifecycleCoroutineScope, viewModel: ItemViewModel) {
        titleBinding.name.text="Add Exercise"
        targetMuscleBinding.input.setText(MuscleGroup.QUADS.toString(), false)
    }


    override fun getContent(): ItemContent? {
        if (!validateName()) {
            return null
        }

        val videoId = validateAndRetrieveVideoIdFromURL(videoTutorialBinding.input.text.toString())
            ?: return null

        return ExerciseContent(
            nameBinding.input.text.toString(),
            notesBinding.input.text.toString(),
            MuscleGroup.valueOf(targetMuscleBinding.input.text.toString()),
            videoId
        )
    }


    private fun validateName(): Boolean {
        if (nameBinding.input.text.isNullOrEmpty()) {
            nameBinding.label.error = context.getString(R.string.item_name_err)
            return false
        }

        return true
    }


    private fun validateAndRetrieveVideoIdFromURL(url: String): String? {
        if (url == "") {
            return ""
        }

        val videoId = YoutubeHelper.getVideoIdFromURL(url)

        if (videoId == null) {
            videoTutorialBinding.label.error = "Failed to interpret as YouTube link."
        }

        return videoId
    }
}