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


enum class TagCategory {
    Intensity
}

class TaggedItem(
    val item: Item
): Item {
    override val id: ItemIdentifier = item.id
    override var name: String = item.name

    private val _tags = mutableMapOf<TagCategory, MutableSet<String>>()
    val tags: Map<TagCategory, Set<String>>  get() = _tags.toMap()

    fun tag(cat: TagCategory, t: String): TaggedItem {

        if (_tags[cat] == null) {
            _tags[cat] = mutableSetOf()
        }

        _tags[cat]!!.add(t)

        return this
    }

    companion object {
        fun makeTagged(item: Item, cat: TagCategory? = null, vararg tags: String): TaggedItem {
            val tagged = TaggedItem(item)

            if (cat != null) {
                for (t in tags) {
                    tagged.tag(cat, t)
                }
            }

            return tagged
        }
    }
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