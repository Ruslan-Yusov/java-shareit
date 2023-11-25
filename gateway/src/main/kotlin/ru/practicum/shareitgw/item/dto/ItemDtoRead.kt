package ru.practicum.shareitgw.item.dto

import ru.practicum.shareitgw.booking.dto.LastBookingDtoRead
import ru.practicum.shareitgw.user.dto.UserDtoRead
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
    var comments: Set<CommentDtoRead>? = null,
    var requestId: Int? = null
)

data class CommentDtoRead(
    var id: Int,
    var text: String,
    var authorName: String,
    var created: LocalDateTime
)
