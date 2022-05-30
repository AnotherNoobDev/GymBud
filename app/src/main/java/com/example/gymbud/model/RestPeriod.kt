package com.example.gymbud.model

import com.example.gymbud.data.ItemIdentifierGenerator

data class RestPeriod(
    override val id: ItemIdentifier,
    override var name: String,
    var targetRestPeriodSec: IntRange
): Item {

    fun getTargetRestPeriodAsString(): String {
        return if (targetRestPeriodSec.first == targetRestPeriodSec.last) {
            targetRestPeriodSec.first.toString() + " sec"
        } else {
            "$targetRestPeriodSec sec"
        }
    }

    companion object {
        val RestDay = RestPeriod(
            ItemIdentifierGenerator.REST_DAY_ID,
            "Rest Day",
            IntRange(86400, 86400)
        )
    }
}

