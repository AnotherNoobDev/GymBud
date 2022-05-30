package com.example.gymbud.data

import com.example.gymbud.model.Item
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.ProgramTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// todo lots of duplication with SetTemplateRepository atm (basically copy-pasta) -> can we do better? (check after adding real data source)
class ProgramTemplateRepository {
    private val _programTemplates: MutableStateFlow<List<ProgramTemplate>> = MutableStateFlow(
        ProgramTemplateDefaultDatasource.programs.toList()
    )
    val programTemplates: StateFlow<List<ProgramTemplate>> = _programTemplates.asStateFlow()

    fun retrieveProgramTemplate(id: ItemIdentifier): ProgramTemplate? = _programTemplates.value.find { it.id == id }


    fun updateProgramTemplate(
        id: ItemIdentifier,
        name: String,
        items: List<Item>
    ) {
        val program = retrieveProgramTemplate(id)
        program?.name = name
        program?.replaceAllWith(items)
    }


    fun addProgramTemplate(
        id: ItemIdentifier,
        name: String,
        items: List<Item>
    ) {
        val newProgram = ProgramTemplate(id, name)
        newProgram.replaceAllWith(items)

        val newProgramTemplates = _programTemplates.value.toMutableSet()
        newProgramTemplates.add(newProgram)
        // todo why complain here about toList but not in other places
        _programTemplates.value = newProgramTemplates.toList()
    }


    fun removeProgramTemplate(id: ItemIdentifier): Boolean {
        val program = retrieveProgramTemplate(id)

        val newProgramTemplates = _programTemplates.value.toMutableList()
        val removed = newProgramTemplates.remove(program)

        if (removed) {
            _programTemplates.value = newProgramTemplates
        }

        return removed
    }
}