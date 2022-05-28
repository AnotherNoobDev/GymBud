package com.example.gymbud.model

typealias ItemIdentifier = Long

enum class ItemType {
    EXERCISE,
    EXERCISE_TEMPLATE,
    REST_PERIOD,
    SET_TEMPLATE,
    WORKOUT_TEMPLATE,
    PROGRAM
}

interface Item {
    val id: ItemIdentifier
    var name: String
}

interface ItemContent {
    var name: String
}


abstract class ItemContainer {
    // todo this is not actually enforced (should it be?)
    abstract fun getSupportedItemTypes(): List<ItemType>

    private var _items: MutableList<Item> = mutableListOf()
    val items: List<Item>
        get() = _items.toList()

    fun add(item: Item): ItemContainer {
        _items.add(item)
        return this
    }

    fun get(index: Int): Item {
        return _items[index]
    }

    fun remove(index: Int): Boolean {
        return try {
            _items.removeAt(index)
            true
        } catch (e: IndexOutOfBoundsException) {
            false
        }
    }

    fun clear() {
        _items.clear()
    }

    fun replaceAllWith(newItems: List<Item>) {
        _items = newItems.toMutableList()
    }
}