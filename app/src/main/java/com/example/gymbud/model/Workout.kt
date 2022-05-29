package com.example.gymbud.model

enum class SetIntensity {
    Warmup,
    Working
}

class WorkoutTemplate(
    override val id: ItemIdentifier,
    override var name: String
): Item, ItemContainer() {
    private val supportedItemTypes = listOf(
        ItemType.SET_TEMPLATE,
        ItemType.REST_PERIOD
    )

    override fun getSupportedItemTypes(): List<ItemType> {
        return supportedItemTypes
    }
}


data class WorkoutTemplateContent(
    override var name: String,
    var items: List<Item>
): ItemContent