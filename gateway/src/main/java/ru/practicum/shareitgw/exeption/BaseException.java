package ru.practicum.shareitgw.exeption;

public abstract class BaseException extends RuntimeException {
    BaseException(String message) {
        super(message);
    }

}
