package com.example.gymbud.model

import androidx.room.Ignore

typealias ItemIdentifier = Long

enum class ItemType {
    EXERCISE,
    EXERCISE_TEMPLATE,
    REST_PERIOD,
    SET_TEMPLATE,
    WORKOUT_TEMPLATE,
    PROGRAM_TEMPLATE,
    UNKNOWN
}

interface Item {
    val id: ItemIdentifier
    var name: String
}

interface ItemContent {
    var name: String
}


fun getValidName(id: ItemIdentifier, name: String, items: List<Item>): String {
    return if (items.find { it.id != id && it.name.trim().lowercase() == name.trim().lowercase() } == null) {
        name
    } else {
        return "$name #$id"
    }
}

fun getItemType(item:  Item): ItemType {
    return when(item) {
        is TaggedItem -> getItemType(item.item)
        is Exercise -> ItemType.EXERCISE
        is ExerciseTemplate -> ItemType.EXERCISE_TEMPLATE
        is RestPeriod -> ItemType.REST_PERIOD
        is SetTemplate -> ItemType.SET_TEMPLATE
        is WorkoutTemplate -> ItemType.WORKOUT_TEMPLATE
        is ProgramTemplate -> ItemType.PROGRAM_TEMPLATE
        else -> ItemType.UNKNOWN
    }
}



enum class TagCategory {
    Intensity
}


typealias Tags = Map<TagCategory, Set<String>>


class TaggedItem(
    val item: Item,
    withTags: Tags = mapOf()
): Item {
    override val id: ItemIdentifier = item.id
    override var name: String = item.name

    private val _tags: MutableMap<TagCategory, MutableSet<String>> = withTags.mapValues { it.value.toMutableSet() }.toMutableMap()
    val tags: Tags get() = _tags.toMap()

    fun tag(cat: TagCategory, t: String): TaggedItem {

        if (_tags[cat] == null) {
            _tags[cat] = mutableSetOf()
        }

        _tags[cat]!!.add(t)

        return this
    }


    override fun equals(other: Any?): Boolean {
        return (other is TaggedItem) && this.item == other.item && this.tags == other.tags
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

    @Ignore private var _items: MutableList<Item> = mutableListOf()
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