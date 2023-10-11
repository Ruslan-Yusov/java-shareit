package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.LastBookingDtoRead
import ru.practicum.shareit.user.dto.UserDtoRead
import java.time.LocalDateTime

/**
 * TODO Sprint add-controllers.
 */
data class ItemDtoRead(
    var id: Int,
    var name: String,
    var owner: UserDtoRead,
    var description: String? = null,
    var available: Boolean? = false,
    var lastBooking: LastBookingDtoRead? = null,
    var nextBooking: LastBookingDtoRead? = null,
    var comments: Set<CommentDtoRead>? = null
)

data class CommentDtoRead(
    var id: Int,
    var text: String,
    var authorName: String,
    var created: LocalDateTime
)
