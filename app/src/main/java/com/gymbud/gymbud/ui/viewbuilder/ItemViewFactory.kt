package com.gymbud.gymbud.ui.viewbuilder

import android.content.Context
import com.gymbud.gymbud.model.Item
import com.gymbud.gymbud.model.ItemType

class ItemViewFactory {
    companion object {
        fun create(
            type: ItemType,
            context: Context,
            onDetailsCallback: (Item) -> Unit
        ): ItemView {
            return when (type) {
                ItemType.EXERCISE -> ExerciseDetailView(context)
                ItemType.EXERCISE_TEMPLATE -> ExerciseTemplateDetailView(onDetailsCallback)
                ItemType.SET_TEMPLATE -> TemplateWithItemsDetailView(context, onDetailsCallback)
                ItemType.WORKOUT_TEMPLATE -> TemplateWithItemsDetailView(context, onDetailsCallback)
                ItemType.PROGRAM_TEMPLATE -> TemplateWithItemsDetailView(context, onDetailsCallback)
                ItemType.REST_PERIOD -> RestPeriodDetailView()
                else -> throw Exception("Can't create ItemView for type:$type")
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
                ItemType.SET_TEMPLATE -> TemplateWithItemsEditView(context, type)
                ItemType.WORKOUT_TEMPLATE -> TemplateWithItemsEditView(context, type)
                ItemType.PROGRAM_TEMPLATE -> TemplateWithItemsEditView(context, type)
                ItemType.REST_PERIOD -> RestPeriodEditView(context)
                else -> throw Exception("Can't create EditItemView for type:$type")
            }
        }
    }
}