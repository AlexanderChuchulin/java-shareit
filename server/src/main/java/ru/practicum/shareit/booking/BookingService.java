package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstraction.ShareItService;
import ru.practicum.shareit.exception.AuthorizationExc;
import ru.practicum.shareit.exception.EntityNotFoundExc;
import ru.practicum.shareit.exception.MainPropDuplicateExc;
import ru.practicum.shareit.exception.ValidationExc;
import ru.practicum.shareit.other.OtherUtils;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
public class BookingService implements ShareItService<BookingDto> {
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
        validateBookingService(bookingMapper.dtoToBooking(bookingDto, userIdHeader), false, conclusion);
        log.info("Create Booking DB");
        return bookingMapper.bookingToDto(bookingJpaRepository.save(bookingMapper.dtoToBooking(bookingDto, userIdHeader)));
    }

    @Override
    public Object getEntityService(Long bookingId, Long userIdHeader, String... additionalParams) {
        if (!userJpaRepository.existsById(userIdHeader)) {
            throw new EntityNotFoundExc("Ошибка поиска Пользователя в БД. " +
                    "Get Bookings by User ID Header " + userIdHeader + " прерван");
        }

        long pageSize = 10;
        List<Booking> bookingPage = new ArrayList<>();
        boolean isStateLikeStatus;

        if (bookingId == null || bookingId == -111222333L) {
            isStateLikeStatus = false;

            if (additionalParams[2] != null) {
                if (!Arrays.toString(BookingState.values())
                        .contains(additionalParams[2].toUpperCase())) {
                    throw new ValidationExc("Unknown state: " + additionalParams[2]);
                } else {
                    isStateLikeStatus = Arrays.toString(BookingStatus.values())
                            .contains(additionalParams[2].toUpperCase());
                }
            }

            if (bookingId == null) {
                if (isStateLikeStatus) {
                    bookingPage = bookingJpaRepository.findAllByBookerIdAndStatus(userIdHeader,
                            BookingStatus.valueOf(additionalParams[2]),
                            OtherUtils.pageableCreateFrommAdditionalParams(additionalParams, pageSize));
                } else {
                    switch (Objects.requireNonNull(additionalParams[2]).toLowerCase()) {
                        case "all":
                            bookingPage = bookingJpaRepository.findAllByBookerIdAndStateAll(userIdHeader,
                                    OtherUtils.pageableCreateFrommAdditionalParams(additionalParams, pageSize));
                            break;
                        case "past":
                            bookingPage = bookingJpaRepository.findAllByBookerIdAndStatePast(userIdHeader, LocalDateTime.now(),
                                    OtherUtils.pageableCreateFrommAdditionalParams(additionalParams, pageSize));
                            break;
                        case "current":
                            bookingPage = bookingJpaRepository.findAllByBookerIdAndStateCurrent(userIdHeader, LocalDateTime.now(),
                                    OtherUtils.pageableCreateFrommAdditionalParams(additionalParams, pageSize));
                            break;
                        case "future":
                            bookingPage = bookingJpaRepository.findAllByBookerIdAndStateFuture(userIdHeader, LocalDateTime.now(),
                                    OtherUtils.pageableCreateFrommAdditionalParams(additionalParams, pageSize));
                            break;
                    }
                }
            } else {
                if (isStateLikeStatus) {
                    bookingPage = bookingJpaRepository.findAllByItemsOwnerIdAndStatus(userIdHeader,
                            BookingStatus.valueOf(additionalParams[2]),
                            OtherUtils.pageableCreateFrommAdditionalParams(additionalParams, pageSize));
                } else {
                    switch (Objects.requireNonNull(additionalParams[2]).toLowerCase()) {
                        case "all":
                            bookingPage = bookingJpaRepository.findAllByItemsOwnerIdAndStateAll(userIdHeader,
                                    OtherUtils.pageableCreateFrommAdditionalParams(additionalParams, pageSize));
                            break;
                        case "past":
                            bookingPage = bookingJpaRepository.findAllByItemsOwnerIdAndStatePast(userIdHeader, LocalDateTime.now(),
                                    OtherUtils.pageableCreateFrommAdditionalParams(additionalParams, pageSize));
                            break;
                        case "current":
                            bookingPage = bookingJpaRepository.findAllByItemsOwnerIdAndStateCurrent(userIdHeader, LocalDateTime.now(),
                                    OtherUtils.pageableCreateFrommAdditionalParams(additionalParams, pageSize));
                            break;
                        case "future":
                            bookingPage = bookingJpaRepository.findAllByItemsOwnerIdAndStateFuture(userIdHeader, LocalDateTime.now(),
                                    OtherUtils.pageableCreateFrommAdditionalParams(additionalParams, pageSize));
                            break;
                    }
                }
            }
            log.info("Get All Bookings by User ID Header " + userIdHeader);
            return bookingPage.stream()
                    .map(bookingMapper::bookingToDto)
                    .collect(Collectors.toList());
        }

        bookingExistCheck(bookingId, "Get Booking by ID " + bookingId);

        if (bookingJpaRepository.getReferenceById(bookingId).getBooker().getUserId() != userIdHeader.intValue()
                && bookingJpaRepository.getReferenceById(bookingId).getBookingItem().getOwner().getUserId() != userIdHeader.intValue()) {
            throw new EntityNotFoundExc("Ошибка авторизации - попытка получить бронирование не владельцем вещи или бронирования. ");
        }
        log.info("Get Booking by Id " + bookingId);
        return bookingMapper.bookingToDto(bookingJpaRepository.getReferenceById(bookingId));
    }

    @Override
    public BookingDto updateEntityService(Long bookingId, BookingDto bookingDto, Long userIdHeader) {
        return null;
    }

    public BookingDto updateBookingService(Long bookingId, Long userIdHeader, Boolean isBookingApproved) {
        bookingExistCheck(bookingId, "Update Booking by id " + bookingId);

        Booking updatingBooking = bookingJpaRepository.getReferenceById(bookingId);

        updatingBooking.setUserIdHeader(userIdHeader);

        if (updatingBooking.getBookingStatus() == BookingStatus.APPROVED & isBookingApproved) {
            throw new ValidationExc("Ошибка обновления. Смена статуса после одобрения.");
        }

        if (isBookingApproved) {
            updatingBooking.setBookingStatus(BookingStatus.APPROVED);
        } else {
            updatingBooking.setBookingStatus(BookingStatus.REJECTED);
        }

        validateBookingService(updatingBooking, true, "Бронирование не обновлено в БД.");

        log.info("Update Booking by ID " + bookingId);
        return bookingMapper.bookingToDto(bookingJpaRepository.save(updatingBooking));
    }

    @Override
    public void deleteEntityService(Long bookingId) {
        if (bookingId == null) {
            bookingJpaRepository.deleteAll();
            log.info("Delete All Items");
        } else {
            bookingExistCheck(bookingId, "Delete Booking by id " + bookingId);
            bookingJpaRepository.deleteById(bookingId);
            log.info("Delete Item by id " + bookingId);
        }
    }

    public void validateBookingService(Booking booking, Boolean isUpdate, String conclusion) {
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
            } else if (booking.getBookingItem() == null
                    || booking.getBookingItem().getOwner().getUserId() != booking.getUserIdHeader().intValue()) {
                excMsg += "Ошибка авторизации - попытка обновить бронирование не владельцем вещи. ";
            }
        }

        if (!isUpdate && (booking.getBookingStart() == null || booking.getBookingEnd() == null
                || booking.getBookingStart().isBefore(LocalDateTime.now()) || booking.getBookingEnd().isBefore(LocalDateTime.now())
                || booking.getBookingEnd().isBefore(booking.getBookingStart()))) {
            excMsg += "Дата и время начала и конца бронирования должны быть заданы и быть позже текущей, " +
                    "дата окончания должна быть после даты начала. ";
        }

        if (excMsg.length() > 0) {
            log.warn("Ошибка валидации бронирования. " + excMsg + conclusion);

            if (excMsg.contains("заголовок с id не задан")) {
                throw new MainPropDuplicateExc(excMsg + conclusion);
            } else if (excMsg.contains("не найден") || excMsg.contains("своей вещи")) {
                throw new EntityNotFoundExc(excMsg + conclusion);
            } else if (excMsg.contains("не владельцем")) {
                throw new AuthorizationExc(excMsg + conclusion);
            } else {
                throw new ValidationExc(excMsg);
            }
        }
    }

    public void bookingExistCheck(Long bookingId, String action) {
        if (!bookingJpaRepository.existsById(bookingId)) {
            throw new EntityNotFoundExc("Ошибка поиска Бронирования в БД. " + action + " прервано.");
        }
    }
}
