package ru.practicum.shareit.item.dto

data class ItemDtoAdd(
    var name: String,
    var description: String? = null,
    var available: Boolean?,
    var ownerId: Int
)
