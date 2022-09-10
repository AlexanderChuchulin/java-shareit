package ru.practicum.shareit.exception;

public class EntityNotFoundExc extends RuntimeException {
    public EntityNotFoundExc(String message) {
        super(message);
    }
}
