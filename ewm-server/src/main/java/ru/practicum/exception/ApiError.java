package ru.practicum.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ApiError {
    private final List<String> errors; // Список стектрейсов ошибок
    private final String message; // Сообщение об ошибке
    private final String reason; // Общее описание причины ошибки
    private final String status; // Код статуса HTTP-ответа
    private final String timestamp; // Дата и время когда произошла ошибка

    public ApiError(List<String> errors, String message, String reason, String status, String timestamp) {
        this.errors = errors;
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
    }
}
