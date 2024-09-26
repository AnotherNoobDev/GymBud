package com.gymbud.gymbud.ui.viewbuilder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import com.gymbud.gymbud.R
import com.gymbud.gymbud.databinding.LayoutDetailDividerBinding
import com.gymbud.gymbud.databinding.LayoutDetailNameBinding
import com.gymbud.gymbud.databinding.LayoutDetailTextFieldBinding
import com.gymbud.gymbud.databinding.LayoutYoutubePlayerBinding
import com.gymbud.gymbud.model.Exercise
import com.gymbud.gymbud.model.Item
import com.gymbud.gymbud.ui.viewmodel.ItemViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener


private const val TAG = "ExerciseDV"


class ExerciseDetailView(val context: Context): ItemView {
    private var _nameBinding: LayoutDetailNameBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _targetMuscleBinding: LayoutDetailTextFieldBinding? = null
    private val targetMuscleBinding get() = _targetMuscleBinding!!

    private var _notesBinding: LayoutDetailTextFieldBinding? = null
    private val notesBinding get() = _notesBinding!!

    private var _youtubePlayerBinding: LayoutYoutubePlayerBinding? = null
    private val youtubePlayerBinding get() = _youtubePlayerBinding!!

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


    override fun populate(
        lifecycle: Lifecycle,
        lifecycleScope: LifecycleCoroutineScope,
        viewModel: ItemViewModel,
        item: Item
    ) {
        if (item !is Exercise) {
            //Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not an exercise!")
            return
        }

        nameBinding.name.text = item.name
        targetMuscleBinding.text.text = item.targetMuscle.toString()
        notesBinding.text.text = item.notes

        if (item.videoTutorial != "") {
            lifecycle.addObserver(youtubePlayerBinding.youtubePlayerView)

            youtubePlayerBinding.noVideoText.visibility = View.GONE
            youtubePlayerBinding.youtubePlayerView.visibility = View.VISIBLE

            videoId = item.videoTutorial

            youtubePlayerBinding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(videoId!!, 0f)
                }
            })
        } else {
            youtubePlayerBinding.noVideoText.visibility = View.VISIBLE
            youtubePlayerBinding.youtubePlayerView.visibility = View.GONE
        }
    }
}