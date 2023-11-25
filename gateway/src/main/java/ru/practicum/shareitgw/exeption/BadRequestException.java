package ru.practicum.shareitgw.exeption;

public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(message);
    }
}
