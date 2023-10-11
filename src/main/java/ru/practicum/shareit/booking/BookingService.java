package ru.practicum.shareit.booking;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.dto.BookingDtoRead;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.ResourceNotFoundException;
import ru.practicum.shareit.item.ItemEntity;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserEntity;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * TODO Sprint add-bookings.
 */
@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    public List<BookingDtoRead> getAllUser(Integer id, String state) {
        if (!Arrays.asList("CURRENT", "PAST", "FUTURE", "ALL", "REJECTED", "WAITING").contains(state)) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя нет"));
        Comparator<BookingDtoRead> comparator = Comparator.comparing(BookingDtoRead::getStart);
        if (!"CURRENT".equals(state)) {
            comparator = comparator.reversed();
        }
        return bookingRepository.findByBookerIdAndState(id, state)
                .stream()
                .map(bookingMapper::entityToBookingDtoRead)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public List<BookingDtoRead> getAllOwner(Integer id, String state) {
        if (!Arrays.asList("CURRENT", "PAST", "FUTURE", "ALL", "REJECTED", "WAITING").contains(state)) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя нет"));
        return bookingRepository.findByOwnerIdAndState(id, state)
                .stream()
                .map(bookingMapper::entityToBookingDtoRead)
                .sorted(Comparator.comparing(BookingDtoRead::getStart).reversed())
                .collect(Collectors.toList());
    }

    public BookingDtoRead getSpecificBooking(Integer idUserOrBooker, Integer idBooking) {
        BookingEntity bookingEntity = bookingRepository.findById(idBooking)
                .orElseThrow(() -> new ResourceNotFoundException("Нет такого бронирования"));
        boolean allowed = ofNullable(bookingEntity.getBooker())
                .map(UserEntity::getId)
                .map(idUserOrBooker::equals)
                .orElse(false)
        || ofNullable(bookingEntity.getItem())
                .map(ItemEntity::getOwner)
                .map(UserEntity::getId)
                .map(idUserOrBooker::equals)
                .orElse(false);
        if (!allowed) {
            throw new ResourceNotFoundException("Права на просмотр у владельца вещи или арендатора");
        }
        return bookingMapper.entityToBookingDtoRead(bookingRepository.findById(idBooking)
                .orElseThrow());
    }

    public BookingDtoRead addBooking(BookingDtoAdd bookingDtoAdd, Integer idUser) {
        ofNullable(bookingDtoAdd.getItemId())
                .orElseThrow(() -> new BadRequestException("Не задан обязательный параметр ItemId"));
        ofNullable(bookingDtoAdd.getStart())
                .orElseThrow(() -> new BadRequestException("Не задан обязательный параметр Start или Start указан позже End"));
        if (LocalDateTime.now().isAfter(bookingDtoAdd.getStart())) {
            throw new BadRequestException("Start не может быть раньше текущей даты");
        }
        if (bookingDtoAdd.getStart().equals(bookingDtoAdd.getEnd())) {
            throw new BadRequestException("Start не может содержать End");
        }
        ofNullable(bookingDtoAdd.getEnd())
                .filter(en -> en.isAfter(bookingDtoAdd.getStart()))
                .orElseThrow(() -> new BadRequestException("Не задан обязательный параметр End или End указан раньше Start"));
        UserEntity user = userRepository.findById(idUser)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя нет"));
        ItemEntity item = itemRepository.findById(bookingDtoAdd.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Такой вещи нет или она не доступна"));
        if (BooleanUtils.isFalse(item.getAvailable())) {
            throw new BadRequestException("Вещь недоступна к бронированию");
        }
        ofNullable(item.getOwner())
                .map(UserEntity::getId)
                .filter(id -> !idUser.equals(id))
                .orElseThrow(() -> new ResourceNotFoundException("владелец не может бронировать свою же вещь, но тесты требуют NOT_FOUND вместо BAD_REQUEST"));
        BookingEntity bookingEntity = bookingMapper.bookingDtoAddToEntity(bookingDtoAdd);
        bookingEntity.setBooker(user);
        bookingEntity.setItem(item);
        bookingEntity.setStatus(Status.WAITING);
        return bookingMapper.entityToBookingDtoRead(bookingRepository.save(bookingEntity));
    }

    public BookingDtoRead updateBookingStatus(Integer idBooking,
                                              Integer idUser,
                                              boolean accept) {
        BookingEntity bookingEntity = bookingRepository.findById(idBooking)
                .orElseThrow(() -> new ResourceNotFoundException("Такого запроса нет"));
        userRepository.findById(idUser)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя нет"));
        ofNullable(bookingEntity.getItem())
                .map(ItemEntity::getOwner)
                .map(UserEntity::getId)
                .filter(idUser::equals)
                .orElseThrow(() -> new ResourceNotFoundException("Данная вещь не принадлежит этому пользователю"));
        if ((bookingEntity.getStatus() == Status.APPROVED && accept)
                || (bookingEntity.getStatus() == Status.REJECTED && !accept)
                || (bookingEntity.getStatus() == Status.CANCELED)) {
            throw new BadRequestException("Нельзя изменить статус заявки");
        }
        bookingEntity.setStatus(accept ? Status.APPROVED : Status.REJECTED);
        return bookingMapper.entityToBookingDtoRead(bookingRepository.save(bookingEntity));
    }
}
