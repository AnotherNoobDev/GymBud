package com.example.gymbud.model

typealias ItemIdentifier = Long

interface Item {
    val id: ItemIdentifier
    var name: String
}