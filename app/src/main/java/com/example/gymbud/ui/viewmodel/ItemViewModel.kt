package com.example.gymbud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymbud.data.repository.ItemRepository
import com.example.gymbud.model.Item
import com.example.gymbud.model.ItemContent
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.ItemType
import kotlinx.coroutines.flow.Flow



class ItemViewModel(
    private val itemRepository: ItemRepository
): ViewModel() {

    fun hasData(): Flow<Boolean> {
        return itemRepository.hasData()
    }

    suspend fun populateWithMinimum() {
        itemRepository.populateWithMinimum()
    }

    suspend fun populateWithDefaults() {
        itemRepository.populateWithDefaults()
    }

    fun getItemsByType(type: ItemType): Flow<List<Item>> {
        return itemRepository.getItemsByType(type)
    }

    fun getItem(id: ItemIdentifier, type: ItemType): Flow<Item?> {
        return itemRepository.getItem(id, type)
    }

    suspend fun getDependantItems(id: ItemIdentifier, type: ItemType): List<String> {
        return itemRepository.getDependantItems(id, type)
    }

    suspend fun addItem(itemContent: ItemContent) {
        itemRepository.addItem(itemContent)
    }

    suspend fun updateItem(id: ItemIdentifier, itemContent: ItemContent) {
        itemRepository.updateItem(id, itemContent)
    }

    suspend fun removeItem(id: ItemIdentifier, type: ItemType? = null) {
        itemRepository.removeItem(id, type)
    }

    fun removeAll() {
        itemRepository.purge()
    }
}


class ItemViewModelFactory(private val itemRepository: ItemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(itemRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}