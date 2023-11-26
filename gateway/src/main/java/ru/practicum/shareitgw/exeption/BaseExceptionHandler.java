package ru.practicum.shareitgw.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareitgw.exeption.dto.ErrorDto;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDto> handleException(BadRequestException ex) {
        return handleException(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorDto> handleException(ConflictException ex) {
        return handleException(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleException(ResourceNotFoundException ex) {
        return handleException(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorDto> handleException(Throwable ex) {
        return handleException(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDto> handleException(Throwable ex, HttpStatus httpStatus) {
        log.error("Error: " + ex.getMessage(), ex);
        ErrorDto errorResponse = ErrorDto.builder()
                .code(httpStatus.value())
                .message(ex.getLocalizedMessage())
                .messageForJuniorTester(ex.getLocalizedMessage())
                .details(Stream.of(ex.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.toList()))
                .build();
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
