package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.user.dto.UserDtoRead

/**
 * TODO Sprint add-controllers.
 */
data class ItemDtoRead(
    var id: Int,
    var name: String,
    var description: String? = null,
    var available: Boolean? = false,
    var owner: UserDtoRead
)
