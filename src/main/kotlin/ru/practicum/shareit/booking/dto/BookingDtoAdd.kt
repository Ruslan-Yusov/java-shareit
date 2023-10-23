package ru.practicum.shareit.booking.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import java.time.LocalDateTime

data class BookingDtoAdd (
    var itemId: Int?,
    @field:JsonFormat(shape = STRING, pattern = "YYYY-MM-DD'T'HH:mm:ss")
    var start: LocalDateTime?,
    @field:JsonFormat(shape = STRING, pattern = "YYYY-MM-DD'T'HH:mm:ss")
    var end: LocalDateTime?,
)