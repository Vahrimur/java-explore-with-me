package ru.practicum.request.model;

public enum RequestStatus {
    PENDING, //В ожидании подтверждения
    CONFIRMED, //Подтверждено
    REJECTED, //Отклонено
    CANCELED //Отменено
}
