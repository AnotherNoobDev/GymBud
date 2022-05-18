package com.example.gymbud.model


interface ProgramDay {
    fun hasWorkout(): Boolean
    fun getWorkout(): WorkoutTemplate?
}


class RestDay: ProgramDay {
    override fun hasWorkout(): Boolean {
        return false
    }

    override fun getWorkout(): WorkoutTemplate? {
        return null
    }
}


class WorkoutDay(
    private val workout: WorkoutTemplate
): ProgramDay {
    override fun hasWorkout(): Boolean {
        return true
    }

    override fun getWorkout(): WorkoutTemplate? {
        return workout
    }
}



class Program (
    override val id: ItemIdentifier,
    override var name: String
): Item {
    private var _days: MutableList<ProgramDay> = mutableListOf()
    val days: List<ProgramDay>
        get() = _days.toList()


    fun addDay(day: ProgramDay): Program {
        _days.add(day)
        return this
    }


    fun retrieveDay(index: Int): ProgramDay {
         return _days[index]
    }


    fun removeDay(index: Int) {
        _days.removeAt(index)
    }


    fun getNumDays(): Int =  _days.size
}