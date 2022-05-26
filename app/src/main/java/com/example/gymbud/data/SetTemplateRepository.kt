package com.example.gymbud.data

import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.SetTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class SetTemplateRepository(
    private val exerciseTemplateRepository: ExerciseTemplateRepository
) {
    private val _setTemplates: MutableStateFlow<List<SetTemplate>> = MutableStateFlow(SetTemplateDefaultDatasource.setTemplatesForHypertrophy)
    val setTemplates: Flow<List<SetTemplate>> =
        _setTemplates.asStateFlow()

    // todo add, remove, update, validate
    fun retrieveSetTemplate(id: ItemIdentifier): SetTemplate? = _setTemplates.value.find { it.id == id }
}