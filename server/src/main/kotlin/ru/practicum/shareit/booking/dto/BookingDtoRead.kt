package ru.practicum.shareit.booking.dto

import ru.practicum.shareit.booking.Status
import ru.practicum.shareit.item.dto.ItemDtoRead
import ru.practicum.shareit.user.dto.UserDtoRead
import java.time.LocalDateTime

data class BookingDtoRead(
    var id: Int,
    var start: LocalDateTime,
    var end: LocalDateTime,
    var item: ItemDtoRead? = null,
    var booker: UserDtoRead,
    var status: Status
)