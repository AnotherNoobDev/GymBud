package com.example.gymbud.ui.viewbuilder

import com.example.gymbud.model.ItemType

class ViewBuilderFactory {
    companion object {
        fun create(type: ItemType): ViewBuilder {
            return when (type) {
                ItemType.EXERCISE -> ExerciseDetailViewBuilder()
                ItemType.EXERCISE_TEMPLATE -> ExerciseTemplateDetailViewBuilder()
                else ->  ExerciseDetailViewBuilder() // todo
            }
        }
    }
}