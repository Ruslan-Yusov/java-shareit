package ru.practicum.shareit.request

import ru.practicum.shareit.item.dto.ItemDtoRead
import java.time.LocalDateTime

data class RequestDtoRead(
    var id: Int,
    var description: String,
    var authorId: Int,
    var created: LocalDateTime,
    var items: Set<ItemDtoRead>? = null
)
