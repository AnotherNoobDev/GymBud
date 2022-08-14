package com.example.gymbud.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gymbud.data.ItemIdentifierGenerator

@Entity(tableName = "rest_period")
data class RestPeriod(
    @PrimaryKey(autoGenerate = false) override val id: ItemIdentifier,
    override var name: String,
    @ColumnInfo(name = "target_in_seconds") var targetRestPeriodSec: IntRange
): Item {

    fun getTargetRestPeriodAsString(): String {
        return if (targetRestPeriodSec.first == targetRestPeriodSec.last) {
            targetRestPeriodSec.first.toString() + " sec"
        } else {
            "$targetRestPeriodSec sec"
        }
    }


    fun isIntraWorkoutRestPeriod(): Boolean = (this != RestDay)


    companion object {
        val RestDay = RestPeriod(
            ItemIdentifierGenerator.REST_DAY_ID,
            "Rest Day",
            IntRange(86400, 86400)
        )
    }
}


data class RestPeriodContent(
    override var name: String,
    var targetRestPeriodSec: IntRange
): ItemContent

