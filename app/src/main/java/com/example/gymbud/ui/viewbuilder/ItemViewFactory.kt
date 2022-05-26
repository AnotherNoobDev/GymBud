package com.example.gymbud.ui.viewbuilder

import android.content.Context
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.ItemType
import com.example.gymbud.ui.viewmodel.ItemViewModel

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
                else ->  ExerciseEditView(context) // todo
            }
        }
    }
}