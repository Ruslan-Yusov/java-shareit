package ru.practicum.shareit.exeption.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@AllArgsConstructor
@Builder
@Getter
@JsonInclude(value = NON_EMPTY)
public class ErrorDto {
    private Integer code;
    private String message;
    @JsonProperty("error")
    private String messageForJuniorTester;
    private List<String> details;
}
