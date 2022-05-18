package com.example.gymbud.data

import com.example.gymbud.model.Program

class ProgramRepository {
    private val _programs: MutableList<Program> = ProgramDefaultDatasource.programs.toMutableList()
    val programs: List<Program>
    get() {
        return _programs.toList()
    }
}