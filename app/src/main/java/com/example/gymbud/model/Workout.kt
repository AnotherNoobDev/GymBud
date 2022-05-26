package com.example.gymbud.model

class WorkoutTemplate(
    override val id: ItemIdentifier,
    override var name: String
): Item, ItemContainer() {
    // list of SetTemplate * how many times to execute ( warmup + working sets) + RestBlocks
    // more flexibility if we just keep it as a list (for executing 3 times, add set template 3 times)
    private val supportedItemTypes = listOf(
        ItemType.SET_TEMPLATE
    )

    override fun getSupportedItemTypes(): List<ItemType> {
        return supportedItemTypes
    }
}

// todo should we tag sets with Warmup, Working?