package com.example.gymbud.model

import androidx.room.*


// note: the list of items is stored in a separate table
@Entity(
    tableName = "program_template"
)
class ProgramTemplate (
    @PrimaryKey(autoGenerate = false) override val id: ItemIdentifier,
    override var name: String
): Item, ItemContainer() {
    @Ignore private val supportedItemTypes = listOf(
        ItemType.WORKOUT_TEMPLATE,
        ItemType.REST_PERIOD
    )

    override fun getSupportedItemTypes(): List<ItemType> {
        return supportedItemTypes
    }
}


data class ProgramTemplateContent(
    override var name: String,
    var items: List<Item>
): ItemContent


// this table contains the items from each program template
@Entity(
    tableName = "program_template_item",
    indices = [
        Index(value = ["program_template_id"]),
        Index(value = ["workout_template_id"]),
        Index(value = ["rest_period_id"]),
        Index(value = ["program_item_pos"])
    ],
    foreignKeys = [
        ForeignKey(entity = ProgramTemplate::class, parentColumns = arrayOf("id"), childColumns = arrayOf("program_template_id"), onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = WorkoutTemplate::class, parentColumns = arrayOf("id"), childColumns = arrayOf("workout_template_id"), onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = RestPeriod::class, parentColumns = arrayOf("id"), childColumns = arrayOf("rest_period_id"), onDelete = ForeignKey.CASCADE),
    ]
)
data class ProgramTemplateWithItem(
    @ColumnInfo(name = "program_template_id") val programTemplateId: ItemIdentifier,
    @ColumnInfo(name = "program_item_pos") var programItemPosition: Int,
    @ColumnInfo(name = "workout_template_id")  val workoutTemplateId: ItemIdentifier? = null,
    @ColumnInfo(name = "rest_period_id")  val restPeriodId: ItemIdentifier? = null,
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0


    fun isWithWorkoutTemplate(): Boolean {
        return workoutTemplateId != null
    }


    fun isWithRestPeriod(): Boolean {
        return restPeriodId != null
    }
}