package com.example.gymbud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymbud.data.ItemRepository
import com.example.gymbud.model.Item
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.ItemType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


class ItemViewModel(
    private val itemRepository: ItemRepository
): ViewModel() {

    fun getItemsByType(type: ItemType): Flow<List<Item>> {
        return itemRepository.getItemsByType(type)
    }

    fun getItem(id: ItemIdentifier, type: ItemType? = null): Item? {
        return itemRepository.getItem(id, type)
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