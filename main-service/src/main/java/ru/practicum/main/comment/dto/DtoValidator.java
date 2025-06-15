package ru.practicum.main.comment.dto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.event.dto.UpdateEventDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.exception.IncorrectlyDateStateRequestException;
import ru.practicum.main.exception.PartialRequestException;
import ru.practicum.main.exception.RequestValidationException;
import ru.practicum.main.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Component
public class DtoValidator {
    LocalDateTime now = LocalDateTime.now();

    public void validateNewCommentDto(NewCommentDto newCommentDto) {
        if (StringUtils.isBlank(newCommentDto.getText())) {
            throw new RequestValidationException(
                    "Не указан текст комментария",
                    "Комментарий пуст",
                    LocalDateTime.now()
            );
        }
    }

    public void validateUpdCommentDto(UpdateCommentDto updateCommentDto) {
        if (StringUtils.isBlank(updateCommentDto.getText())) {
            throw new RequestValidationException(
                    "Не указан текст комментария",
                    "Комментарий пуст",
                    LocalDateTime.now()
            );
        }
    }

    public void updValidationDtoForAdmin(Event stored, UpdateEventDto updateEventDto) {
        if (!Objects.equals(EventState.PUBLISHED, stored.getState())) {
            throw new IncorrectlyDateStateRequestException(
                    "Условия выполнения не соблюдены",
                    "Изменять можно неопубликованные события",
                    now);
        }
        if (stored.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new IncorrectlyDateStateRequestException(
                    "Неверно указана дата события",
                    "Дата события не может быть менее чем за 1 час до начала",
                    now);
        }
        if (updateEventDto.getEventDate() != null) {
            if (updateEventDto.getEventDate().isBefore(LocalDateTime.now())) {
                throw new IncorrectlyDateStateRequestException(
                        "Условия выполнения не соблюдены",
                        "Новое время в прошлом",
                        now);
            }
        }
    }

    public void updValidationDtoForUser(Long userId, UpdateEventDto updateEventDto, Event stored) {
        if (!stored.getInitiator().equals(userId)) {
            throw new IncorrectlyDateStateRequestException(
                    "Условия выполнения не соблюдены",
                    "Изменять может только владелец",
                    now);
        }
        if (stored.getState().equals(EventState.PUBLISHED)) {
            throw new IncorrectlyDateStateRequestException(
                    "Условия выполнения не соблюдены",
                    "Изменять можно неопубликованные события",
                    now);
        }
        if (updateEventDto.getEventDate() != null) {
            if (updateEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new IncorrectlyDateStateRequestException(
                        "Условия выполнения не соблюдены",
                        "Изменять можно события за 2 часа до начала",
                        now);
            }
        }
        if (stored.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectlyDateStateRequestException(
                    "Условия выполнения не соблюдены",
                    "Изменять можно события за 2 часа до начала",
                    now);
        }
        if (stored.getParticipantLimit() == 0) {
            throw new PartialRequestException("Мест нет",
                    "Нет свободных мест в событиии", LocalDateTime.now());
        }
    }

    public void validateCategory(CategoryDto category) {
        if (StringUtils.isBlank(category.getName())) {
            throw new RequestValidationException(
                    "Не указано имя категории",
                    "Имя категории пустое",
                    now
            );
        }
    }

    public void validateCategoryForUpd(CategoryDto category) {
        if (StringUtils.isBlank(category.getName())) {
            throw new RequestValidationException(
                    "Не указано имя категории",
                    "Имя категории пустое",
                    now
            );
        }
    }

    public void validateUserDto(UserDto user) {
        if (StringUtils.isBlank(user.getName())) {
            throw new RequestValidationException(
                    "Не указано имя пользователя",
                    "Имя пользователя пустое",
                    now
            );
        }
    }

    public void validateNewEventDto(NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectlyDateStateRequestException(
                    "Неверно указана дата события",
                    "Дата события не может быть в прошлом или ранее 2-х часов",
                   now
            );
        }
        if (newEventDto.getCategory() <= 0) {
            throw new RequestValidationException(
                    "Неверно указана категория указано имя пользователя",
                    "Имя пользователя пустое",
                    now
            );
        }
        if (StringUtils.isBlank(newEventDto.getAnnotation())) {
            throw new RequestValidationException(
                    "Не указана аннотация",
                    "Поле аннотации пустое",
                   now
            );
        }
    }

    public void validateUpdateEventDto(UpdateEventDto updateEventDto) {
        if (null != updateEventDto.getEventDate()) {
            if (updateEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new IncorrectlyDateStateRequestException(
                        "Неверно указана дата события",
                        "Дата события не может быть в прошлом или ранее 2-х часов",
                        now
                );
            }
        }
    }
}
