package com.example.gymbud.ui.viewbuilder

import android.content.Context
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.ItemType

class ItemViewFactory {
    companion object {
        fun create(
            type: ItemType,
            onDetailsCallback: (ItemIdentifier, ItemType) -> Unit
        ): ItemView {
            return when (type) {
                ItemType.EXERCISE -> ExerciseDetailView()
                ItemType.EXERCISE_TEMPLATE -> ExerciseTemplateDetailView(onDetailsCallback)
                ItemType.SET_TEMPLATE -> SetTemplateDetailView(onDetailsCallback)
                ItemType.WORKOUT_TEMPLATE -> WorkoutTemplateDetailView(onDetailsCallback)
                else ->  ExerciseDetailView() // todo
            }
        }
    }
}


class EditItemViewFactory {
    companion object {
        fun create(type: ItemType, context: Context): EditItemView {
            return when (type) {
                ItemType.EXERCISE -> ExerciseEditView(context)
                ItemType.EXERCISE_TEMPLATE -> ExerciseTemplateEditView(context)
                ItemType.SET_TEMPLATE -> SetTemplateEditView(context)
                ItemType.WORKOUT_TEMPLATE -> WorkoutTemplateEditView(context)
                else ->  ExerciseEditView(context) // todo
            }
        }
    }
}