package com.example.gymbud.model

typealias ItemIdentifier = Long

enum class ItemType {
    EXERCISE,
    EXERCISE_TEMPLATE,
    SET_TEMPLATE,
    WORKOUT_TEMPLATE,
    PROGRAM
}

interface Item {
    val id: ItemIdentifier
    var name: String
}