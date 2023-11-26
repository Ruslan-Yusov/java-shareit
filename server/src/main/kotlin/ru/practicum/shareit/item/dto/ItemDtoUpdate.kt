package ru.practicum.shareit.item.dto

data class ItemDtoUpdate(
    var name: String? = null,
    var description: String? = null,
    var available: Boolean? = null,
)
