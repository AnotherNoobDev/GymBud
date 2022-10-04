package com.gymbud.gymbud.model

import androidx.room.*


// note: the list of items is stored in a separate table
@Entity(
    tableName = "set_template"
)
class SetTemplate(
    @PrimaryKey(autoGenerate = false) override val id: ItemIdentifier,
    override var name: String
): Item, ItemContainer() {

    @Ignore private val supportedItemTypes = listOf(
        ItemType.EXERCISE_TEMPLATE,
        ItemType.SET_TEMPLATE,
        ItemType.REST_PERIOD
    )


    override fun getSupportedItemTypes(): List<ItemType> {
        return supportedItemTypes
    }


    override fun equals(other: Any?): Boolean {
        return (other is SetTemplate) && other.id == this.id && other.name == this.name && other.items == this.items
    }


    override fun hashCode(): Int {
        return id.hashCode()
    }
}


data class SetTemplateContent(
    override var name: String,
    var items: List<Item>
): ItemContent


// this table contains the items from each set template
@Entity(
    tableName = "set_template_item",
    indices = [
        Index(value = ["set_template_id"]),
        Index(value = ["exercise_template_id"]),
        Index(value = ["rest_period_id"]),
        Index(value = ["set_item_pos"])
    ],
    foreignKeys = [
        ForeignKey(entity = SetTemplate::class, parentColumns = arrayOf("id"), childColumns = arrayOf("set_template_id"), onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ExerciseTemplate::class, parentColumns = arrayOf("id"), childColumns = arrayOf("exercise_template_id"), onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = RestPeriod::class, parentColumns = arrayOf("id"), childColumns = arrayOf("rest_period_id"), onDelete = ForeignKey.CASCADE),
    ]
)
data class SetTemplateWithItem(
    @ColumnInfo(name = "set_template_id") val setTemplateId: ItemIdentifier,
    @ColumnInfo(name = "set_item_pos") var setItemPosition: Int,
    @ColumnInfo(name = "exercise_template_id")  val exerciseTemplateId: ItemIdentifier? = null,
    @ColumnInfo(name = "rest_period_id")  val restPeriodId: ItemIdentifier? = null,
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0


    fun isWithExerciseTemplate(): Boolean {
        return exerciseTemplateId != null
    }


    fun isWithRestPeriod(): Boolean {
        return restPeriodId != null
    }
}