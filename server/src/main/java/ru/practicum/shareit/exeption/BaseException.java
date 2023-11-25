package ru.practicum.shareit.exeption;

public abstract class BaseException extends RuntimeException {
    BaseException(String message) {
        super(message);
    }

}
