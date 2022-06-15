package com.example.gymbud.data

import com.example.gymbud.BaseApplication
import com.example.gymbud.model.ItemIdentifier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


object ItemIdentifierGenerator {
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

    private var seeded = false
    private var app: BaseApplication? = null

    private var id: ItemIdentifier = NO_ID

    fun setApp(app: BaseApplication) {
        this.app = app
    }


    fun reset() {
        seeded = false
    }


    fun generateId():  ItemIdentifier {
        if (!seeded) {
            seed()
        }

        // we need to ensure that we never give out an id that wasn't persisted
        // this means we want to block while we persist
        runBlocking {
            app!!.appRepository.updateLastUsedItemIdentifier(id)
        }

        val nextId = id
        id += 1

        // then return the id
        return nextId
    }


    private fun seed() {
        runBlocking {
            // we first need to sync up with the persisted id
            id = app!!.appRepository.lastItemIdentifier.first()

            // then set the id in a correct state
            if (id == NO_ID) {
                // todo as a fallback when no id is found in the datastore,
                // we could look at the database and determine the next id
                id = 0
            } else {
                id += 1
            }

            // now we are ready to generate an id
            seeded = true
        }
    }
}