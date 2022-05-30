package com.example.gymbud.model



class ProgramTemplate (
    override val id: ItemIdentifier,
    override var name: String
): Item, ItemContainer() {
    private val supportedItemTypes = listOf(
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