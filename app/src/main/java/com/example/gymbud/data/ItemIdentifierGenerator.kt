package com.example.gymbud.data

import com.example.gymbud.model.ItemIdentifier

// todo this is very simplistic atm :)
object ItemIdentifierGenerator {
    private var id: ItemIdentifier = 0 // todo needs to be persisted

    const val NO_ID: ItemIdentifier = -1

    const val REST_DAY_ID: ItemIdentifier = -100

    const val REST30_ID: ItemIdentifier = -10030
    const val REST60_ID: ItemIdentifier = -10060
    const val REST120_ID: ItemIdentifier = -100120
    const val REST180_ID: ItemIdentifier = -100180

    const val REST30_TO_60_ID: ItemIdentifier = -1003060
    const val REST60_TO_120_ID: ItemIdentifier = -10060120
    const val REST60_TO_180_ID: ItemIdentifier = -10060180
    const val REST120_TO_180_ID: ItemIdentifier = -100120180
    const val REST180_TO_300_ID: ItemIdentifier = -100180300


    fun generateId():  ItemIdentifier {
        return id++
    }
}