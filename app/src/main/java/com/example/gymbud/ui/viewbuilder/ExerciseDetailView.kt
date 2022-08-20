package com.example.gymbud.ui.viewbuilder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gymbud.AppConfig
import com.example.gymbud.R
import com.example.gymbud.databinding.*
import com.example.gymbud.model.Exercise
import com.example.gymbud.model.Item
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX


private const val TAG = "ExerciseDV"


class ExerciseDetailView: ItemView {
    private var _nameBinding: LayoutDetailNameBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _targetMuscleBinding: LayoutDetailTextFieldBinding? = null
    private val targetMuscleBinding get() = _targetMuscleBinding!!

    private var _notesBinding: LayoutDetailTextFieldBinding? = null
    private val notesBinding get() = _notesBinding!!

    private var _youtubePlayerBinding: LayoutYoutubePlayerBinding? = null
    private val youtubePlayerBinding get() = _youtubePlayerBinding!!

    private var youtubePlayer: YouTubePlayer? = null
    private var videoId: String? = null


    override fun inflate(inflater: LayoutInflater): List<View> {
        _nameBinding = LayoutDetailNameBinding.inflate(inflater)

        _targetMuscleBinding = LayoutDetailTextFieldBinding.inflate(inflater)
        targetMuscleBinding.icon.setImageResource(R.drawable.ic_target_muscle_24)

        val divider1 = LayoutDetailDividerBinding.inflate(inflater).root

        _notesBinding = LayoutDetailTextFieldBinding.inflate(inflater)
        notesBinding.icon.setImageResource(R.drawable.ic_notes_24)
        notesBinding.text.isSingleLine = false

        val divider2 = LayoutDetailDividerBinding.inflate(inflater).root

        _youtubePlayerBinding = LayoutYoutubePlayerBinding.inflate(inflater)


        return listOf(
            nameBinding.root,
            targetMuscleBinding.root,
            divider1,
            notesBinding.root,
            divider2,
            youtubePlayerBinding.root
        )
    }


    override fun performTransactions(fragmentManager: FragmentManager) {
        val youTubePlayerFragment = YouTubePlayerSupportFragmentX.newInstance()

        youTubePlayerFragment.initialize(
            AppConfig.youtubeApiKey,
            object : YouTubePlayer.OnInitializedListener {

                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider,
                    player: YouTubePlayer, b: Boolean
                ) {
                    youtubePlayer = player

                    prepareYoutubePlayer()
                }

                override fun onInitializationFailure(
                    provider: YouTubePlayer.Provider,
                    youTubeInitializationResult: YouTubeInitializationResult
                ) {
                    Log.e(TAG, "Failed to initialize YoutubePlayer")
                }
            }
        )

        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.youtubePlayerPlaceholder, youTubePlayerFragment).commit()
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

        nameBinding.name.text = item.name
        targetMuscleBinding.text.text = item.targetMuscle.toString()
        notesBinding.text.text = item.notes

        if (item.videoTutorial != "") {
            youtubePlayerBinding.noVideoText.visibility = View.GONE
            youtubePlayerBinding.youtubePlayerPlaceholder.visibility = View.VISIBLE

            videoId = item.videoTutorial
            prepareYoutubePlayer()
        } else {
            youtubePlayerBinding.noVideoText.visibility = View.VISIBLE
            youtubePlayerBinding.youtubePlayerPlaceholder.visibility = View.GONE
        }
    }


    private fun prepareYoutubePlayer() {
        if (youtubePlayer == null || videoId == null) {
            return
        }

        youtubePlayer!!.cueVideo(videoId!!)
    }
}