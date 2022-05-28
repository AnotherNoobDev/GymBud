package com.example.gymbud.model

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
}