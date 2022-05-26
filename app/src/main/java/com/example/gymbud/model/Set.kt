package com.example.gymbud.model

open class SetTemplate(
    override val id: ItemIdentifier,
    override var name: String,
): Item, ItemContainer() {
    private val supportedItemTypes = listOf(
        ItemType.EXERCISE_TEMPLATE,
        ItemType.SET_TEMPLATE
    )

    override fun getSupportedItemTypes(): List<ItemType> {
        return supportedItemTypes
    }
}