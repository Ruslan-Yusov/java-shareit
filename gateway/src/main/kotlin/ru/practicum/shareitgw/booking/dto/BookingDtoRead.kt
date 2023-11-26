package ru.practicum.shareitgw.booking.dto

import ru.practicum.shareitgw.booking.Status
import ru.practicum.shareitgw.item.dto.ItemDtoRead
import ru.practicum.shareitgw.user.dto.UserDtoRead
import java.time.LocalDateTime

data class BookingDtoRead(
    var id: Int,
    var start: LocalDateTime,
    var end: LocalDateTime,
    var item: ItemDtoRead? = null,
    var booker: UserDtoRead,
    var status: Status
)