package com.example.gymbud.ui.viewbuilder

import android.view.LayoutInflater
import android.view.View
import com.example.gymbud.model.Item

interface ViewBuilder {
    fun inflate(inflater: LayoutInflater): List<View>
    fun populate(item: Item)
}