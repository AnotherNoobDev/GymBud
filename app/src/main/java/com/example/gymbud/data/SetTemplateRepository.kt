package com.example.gymbud.data

import com.example.gymbud.model.Item
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.SetTemplate
import com.example.gymbud.model.getValidName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class SetTemplateRepository(
    private val exerciseTemplateRepository: ExerciseTemplateRepository
) {
    private val _setTemplates: MutableStateFlow<List<SetTemplate>> = MutableStateFlow(SetTemplateDefaultDatasource.setTemplatesForHypertrophy)
    val setTemplates: Flow<List<SetTemplate>> =
        _setTemplates.asStateFlow()

    fun retrieveSetTemplate(id: ItemIdentifier): SetTemplate? = _setTemplates.value.find { it.id == id }


    fun updateSetTemplate(
        id: ItemIdentifier,
        name: String,
        items: List<Item>
    ) {
        val validName = getValidName(id, name, _setTemplates.value)

        val set = retrieveSetTemplate(id)
        set?.name = validName
        set?.replaceAllWith(items)
    }


    fun addSetTemplate(
        id: ItemIdentifier,
        name: String,
        items: List<Item>
    ) {
        val validName = getValidName(id, name, _setTemplates.value)

        val newSet = SetTemplate(id, validName)
        newSet.replaceAllWith(items)

        val newSetTemplates = _setTemplates.value.toMutableSet()
        newSetTemplates.add(newSet)
        // todo why complain here about toList but not in other places
        _setTemplates.value = newSetTemplates.toList()
    }


    fun removeSetTemplate(id: ItemIdentifier): Boolean {
        val set = retrieveSetTemplate(id)

        val newSetTemplates = _setTemplates.value.toMutableList()
        val removed = newSetTemplates.remove(set)

        if (removed) {
            _setTemplates.value = newSetTemplates
        }

        return removed
    }
}