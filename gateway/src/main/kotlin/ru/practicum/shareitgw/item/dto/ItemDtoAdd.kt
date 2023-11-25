package ru.practicum.shareitgw.item.dto

data class ItemDtoAdd(
    var name: String,
    var description: String? = null,
    var available: Boolean?,
    var ownerId: Int,
    var requestId: Int? = null
)
