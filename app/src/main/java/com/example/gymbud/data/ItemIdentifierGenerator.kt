package com.example.gymbud.data

import com.example.gymbud.model.ItemIdentifier

// todo this is very simplistic atm :)
object ItemIdentifierGenerator {
    private var id: ItemIdentifier = 0

    fun generateId():  ItemIdentifier {
        return id++
    }

    fun generateTempId(): ItemIdentifier {
        return -1
    }
}