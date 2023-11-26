package ru.practicum.shareitgw.booking.dto

import ru.practicum.shareitgw.booking.Status
import java.time.LocalDateTime

data class LastBookingDtoRead(
    var id: Int,
    var start: LocalDateTime,
    var end: LocalDateTime,
    var bookerId: Int,
    var status: Status
)