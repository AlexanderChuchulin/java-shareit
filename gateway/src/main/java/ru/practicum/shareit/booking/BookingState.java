package ru.practicum.shareit.booking;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static boolean stateCheck(String bookingState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(bookingState)) {
                return true;
            }
        }
        return false;
    }
}
