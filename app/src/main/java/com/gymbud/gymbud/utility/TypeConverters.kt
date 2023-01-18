package com.gymbud.gymbud.utility

import com.gymbud.gymbud.model.TagCategory
import com.gymbud.gymbud.model.Tags


/// IntRange <--> String (first..last)

private const val DelimiterBetweenFirstAndLast = ".."

fun convertIntRangeFromString(value: String?): IntRange? {
    return value?.let {
        val tokenized = value.split(DelimiterBetweenFirstAndLast)

        return@let if(tokenized.size != 2) {
            null
        } else {
            IntRange(tokenized[0].toInt(), tokenized[1].toInt())
        }
    }
}


fun convertIntRangeToString(range: IntRange?): String? {
    return range?.let { "${range.first}${DelimiterBetweenFirstAndLast}${range.last}" }
}


/// Tags <--> String (tagCategory1:tagValue1,tagValue2,tagValue3|tagCategory2:tagValue1,tagValue2)

private const val DelimiterBetweenCategories = "|"
private const val DelimiterBetweenCategoryAndValues = ":"
private const val DelimiterBetweenValues = ","

fun convertTagsFromString(value: String?): Tags? {
    return value?.let {
        val tags: MutableMap<TagCategory, Set<String>> = mutableMapOf()

        if (value.isEmpty()) return@let tags.toMap()

        value.split(DelimiterBetweenCategories).forEach{ categoryWithValues ->
            val (category, values) = categoryWithValues.split(DelimiterBetweenCategoryAndValues)
            tags[TagCategory.valueOf(category)] = values.split(DelimiterBetweenValues).toSet()
        }

        return@let tags.toMap()
    }
}


fun convertTagsToString(tags: Tags?): String? {
    return tags?.let {
        tags.entries.joinToString(separator = DelimiterBetweenCategories) {
            "${it.key}${DelimiterBetweenCategoryAndValues}${it.value.joinToString(separator=DelimiterBetweenValues)}"
        }
    }
}
