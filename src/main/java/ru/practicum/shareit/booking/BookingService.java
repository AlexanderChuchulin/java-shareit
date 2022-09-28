package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.ShareItService;
import ru.practicum.shareit.exception.AuthorizationExc;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.exception.MainPropDuplicateExc;
import ru.practicum.shareit.exception.ValidationExc;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
public class BookingService implements ShareItService<Booking, BookingDto> {
    private final BookingJpaRepository bookingJpaRepository;
    private final BookingMapper bookingMapper;
    private final UserJpaRepository userJpaRepository;

    public BookingService(BookingJpaRepository bookingJpaRepository,
                          BookingMapper bookingMapper, UserJpaRepository userJpaRepository) {
        this.bookingJpaRepository = bookingJpaRepository;
        this.bookingMapper = bookingMapper;
        this.userJpaRepository = userJpaRepository;
    }


    @Override
    public BookingDto createEntityService(BookingDto bookingDto, Long userIdHeader) {
        String conclusion = "Бронирование не создано в БД.";

        bookingDto.setBookingStatus(BookingStatus.WAITING);
        validateEntityService(bookingMapper.dtoToBooking(bookingDto, userIdHeader), false, conclusion);
        log.info("Create User DB " + bookingMapper.dtoToBooking(bookingDto, userIdHeader));
        return bookingMapper.bookingToDto(bookingJpaRepository.save(bookingMapper.dtoToBooking(bookingDto, userIdHeader)));
    }

    @Override
    public Object getEntityService(Long bookingId, Long userIdHeader, String... bookingState) {
        userJpaRepository.entityExistCheck(userIdHeader, "Get Bookings by User ID Header " + userIdHeader + " прерван");

        List<Booking> bookingList = new ArrayList<>();
        boolean isStateLikeStatus;

        if (bookingId == null || bookingId == -111222333L) {
            isStateLikeStatus = false;

            if (bookingState[0] != null) {
                if (!Arrays.toString(BookingState.values()).contains(bookingState[0].toUpperCase())) {
                    throw new ValidationExc("Unknown state: " + bookingState[0]);
                } else {
                    isStateLikeStatus = Arrays.toString(BookingStatus.values()).contains(bookingState[0].toUpperCase());
                }
            }

            if (bookingId == null) {
                if (isStateLikeStatus) {
                    bookingList = bookingJpaRepository.findAllByBookerIdAndStatus(userIdHeader, BookingStatus.valueOf(bookingState[0]));
                } else {
                    switch (Objects.requireNonNull(bookingState[0]).toLowerCase()) {
                        case "all":
                            bookingList = bookingJpaRepository.findAllByBookerIdAndStateAll(userIdHeader);
                            break;
                        case "past":
                            bookingList = bookingJpaRepository.findAllByBookerIdAndStatePast(userIdHeader, LocalDateTime.now());
                            break;
                        case "current":
                            bookingList = bookingJpaRepository.findAllByBookerIdAndStateCurrent(userIdHeader, LocalDateTime.now());
                            break;
                        case "future":
                            bookingList = bookingJpaRepository.findAllByBookerIdAndStateFuture(userIdHeader, LocalDateTime.now());
                            break;
                    }
                }
            } else {
                if (isStateLikeStatus) {
                    bookingList = bookingJpaRepository.findAllByItemsOwnerIdAndStatus(userIdHeader, BookingStatus.valueOf(bookingState[0]));
                } else {
                    switch (Objects.requireNonNull(bookingState[0]).toLowerCase()) {
                        case "all":
                            bookingList = bookingJpaRepository.findAllByItemsOwnerIdAndStateAll(userIdHeader);
                            break;
                        case "past":
                            bookingList = bookingJpaRepository.findAllByItemsOwnerIdAndStatePast(userIdHeader, LocalDateTime.now());
                            break;
                        case "current":
                            bookingList = bookingJpaRepository.findAllByItemsOwnerIdAndStateCurrent(userIdHeader, LocalDateTime.now());
                            break;
                        case "future":
                            bookingList = bookingJpaRepository.findAllByItemsOwnerIdAndStateFuture(userIdHeader, LocalDateTime.now());
                            break;
                    }
                }
            }
            log.info("Get All Bookings by User ID Header");
            return bookingList.stream()
                    .map(bookingMapper::bookingToDto)
                    .collect(Collectors.toList());
        }

        bookingJpaRepository.entityExistCheck(bookingId, "Get Booking by ID " + bookingId);

        if (bookingJpaRepository.getReferenceById(bookingId).getBooker().getUserId() != userIdHeader.intValue()
                & bookingJpaRepository.getReferenceById(bookingId).getBookingItem().getOwner().getUserId() != userIdHeader.intValue()) {
            throw new EntityNotFoundExc("Ошибка авторизации - попытка получить бронирование не владельцем вещи или бронирования. ");
        }
        log.info("Get Booking by Id " + bookingId);
        return bookingMapper.bookingToDto(bookingJpaRepository.getReferenceById(bookingId));
    }

    @Override
    public BookingDto updateEntityService(Long bookingId, BookingDto bookingDto, Long userIdHeader) {
        Booking updatingBooking = bookingMapper.dtoToBooking(bookingDto, userIdHeader);

        log.info("Update Booking by ID " + bookingId);
        return bookingMapper.bookingToDto(bookingJpaRepository.save(updatingBooking));
    }

    public BookingDto updateBookingService(Long bookingId, Long userIdHeader, Boolean isBookingApproved) {
        log.info("Update Booking by ID " + bookingId);
        return bookingMapper.bookingToDto(bookingJpaRepository.
                save(bookingJpaRepository.updateBookingById(bookingId, userIdHeader, isBookingApproved, this)));
    }

    @Override
    public void deleteEntityService(Long bookingId) {
        if (bookingId == null) {
            bookingJpaRepository.deleteAll();
            log.info("Delete All Items");
        } else {
            bookingJpaRepository.entityExistCheck(bookingId, "Delete Booking by id " + bookingId);
            bookingJpaRepository.deleteById(bookingId);
            log.info("Delete Item by id " + bookingId);
        }
    }

    @Override
    public void validateEntityService(Booking booking, Boolean isUpdate, String conclusion) {
        String excMsg = "";

        if (booking.getBookingItem() == null) {
            excMsg += "Вещь не найдена. ";
        } else if (!booking.getBookingItem().getIsItemAvailable()) {
            excMsg += "Вещь с id " + booking.getBookingItem().getItemId() + " не доступна для бронирования. ";
        } else if (!isUpdate && booking.getBooker().getUserId() == booking.getBookingItem().getOwner().getUserId().intValue()) {
            excMsg += "Попытка бронирования своей вещи. ";
        }

        if (booking.getUserIdHeader() == null) {
            excMsg += "Ошибка авторизации - заголовок с id не задан. ";
        } else if (!userJpaRepository.existsById(booking.getUserIdHeader())) {
            excMsg += "Пользователь по UseridHeader " + booking.getUserIdHeader() + " не найден. ";
        } else if (isUpdate) {
            if (booking.getBooker().getUserId() == booking.getUserIdHeader().intValue()) {
                throw new EntityNotFoundExc("Попытка обновления статуса одобрения владельцем бронирования.");
            } else if (booking.getBookingItem().getOwner().getUserId() != booking.getUserIdHeader().intValue()) {
                excMsg += "Ошибка авторизации - попытка обновить бронирование не владельцем вещи. ";
            }
        }

        if (booking.getBookingStart().isBefore(LocalDateTime.now()) || booking.getBookingEnd().isBefore(LocalDateTime.now())
                || booking.getBookingEnd().isBefore(booking.getBookingStart())) {
            excMsg += "Дата и время начала и конца бронирования должны быть позже текущей, " +
                    "дата окончания должна быть после даты начала. ";
        }

        if (excMsg.length() > 0) {
            log.warn("Ошибка валидации вещи. " + excMsg + conclusion);

            if (excMsg.contains("заголовок с id не задан")) {
                throw new MainPropDuplicateExc(excMsg + conclusion);
            } else if (excMsg.contains("не найден") | excMsg.contains("своей вещи")) {
                throw new EntityNotFoundExc(excMsg + conclusion);
            } else if (excMsg.contains("не владельцем")) {
                throw new AuthorizationExc(excMsg + conclusion);
            } else {
                throw new ValidationExc(excMsg);
            }
        }
    }
}
