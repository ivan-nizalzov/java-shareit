package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.UnsupportedStatus;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static BookingState getStateFromText(String text) {
        for (BookingState state : BookingState.values()) {
            if (state.toString().equals(text)) {
                return state;
            }
        }
        throw new UnsupportedStatus("Unknown state: " + text);
    }
}
