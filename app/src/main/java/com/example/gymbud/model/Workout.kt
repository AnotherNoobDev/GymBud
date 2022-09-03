package com.example.gymbud.model

import androidx.room.*


enum class SetIntensity {
    Warmup,
    Working
}


// note: the list of items is stored in a separate table
@Entity(
    tableName = "workout_template"
)
class WorkoutTemplate(
    @PrimaryKey(autoGenerate = false) override val id: ItemIdentifier,
    override var name: String
): Item, ItemContainer() {
    @Ignore private val supportedItemTypes = listOf(
        ItemType.SET_TEMPLATE,
        ItemType.REST_PERIOD
    )


    override fun getSupportedItemTypes(): List<ItemType> {
        return supportedItemTypes
    }


    override fun equals(other: Any?): Boolean {
        return (other is WorkoutTemplate) && other.id == this.id && other.name == this.name && other.items == this.items
    }
}


data class WorkoutTemplateContent(
    override var name: String,
    var items: List<Item>
): ItemContent


// this table contains the items from each workout template
@Entity(
    tableName = "workout_template_item",
    indices = [
        Index(value = ["workout_template_id"]),
        Index(value = ["set_template_id"]),
        Index(value = ["rest_period_id"]),
        Index(value = ["workout_item_pos"])
    ],
    foreignKeys = [
        ForeignKey(entity = WorkoutTemplate::class, parentColumns = arrayOf("id"), childColumns = arrayOf("workout_template_id"), onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = SetTemplate::class, parentColumns = arrayOf("id"), childColumns = arrayOf("set_template_id"), onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = RestPeriod::class, parentColumns = arrayOf("id"), childColumns = arrayOf("rest_period_id"), onDelete = ForeignKey.CASCADE),
    ]
)
data class WorkoutTemplateWithItem(
    @ColumnInfo(name = "workout_template_id") val workoutTemplateId: ItemIdentifier,
    @ColumnInfo(name = "workout_item_pos") var workoutItemPosition: Int,
    @ColumnInfo(name = "set_template_id")  val setTemplateId: ItemIdentifier? = null,
    @ColumnInfo(name = "rest_period_id")  val restPeriodId: ItemIdentifier? = null,
    val tags: Tags = mapOf()
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0


    fun isWithSetTemplate(): Boolean {
        return setTemplateId != null
    }


    fun isWithRestPeriod(): Boolean {
        return restPeriodId != null
    }
}