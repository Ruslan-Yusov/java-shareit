package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.dto.BookingDtoRead;
import ru.practicum.shareit.booking.dto.LastBookingDtoRead;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, UserMapper.class})
public interface BookingMapper {

    @Mapping(target = "start", source = "startDateTime")
    @Mapping(target = "end", source = "endDateTime")
    BookingDtoRead entityToBookingDtoRead(BookingEntity value);

    @Mapping(target = "start", source = "startDateTime")
    @Mapping(target = "end", source = "endDateTime")
    @Mapping(target = "bookerId", source = "booker.id")
    LastBookingDtoRead entityToBookingDtoReadSimple(BookingEntity value);

    @Mapping(target = "startDateTime", source = "start")
    @Mapping(target = "endDateTime", source = "end")
    BookingEntity bookingDtoAddToEntity(BookingDtoAdd value);

}
