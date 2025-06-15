package ru.practicum.main.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.service.CommentService;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.event.dto.UpdateEventDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.request.service.RequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping
public class PrivateController {
    private final EventService eventService;
    private final RequestService requestService;
    private final CommentService commentService;

    @PostMapping("/users/{userId}/events")
    public ResponseEntity<Object> createEvent(@PathVariable("userId") Long id,
                                              @RequestBody NewEventDto newEvent) {
        log.info("Создание нового события в категории {}", newEvent.getCategory());
        return new ResponseEntity<>(eventService.create(id, newEvent), HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/events")
    public ResponseEntity<Object> getEventsByUserId(@PathVariable("userId") Long id,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                    @Positive @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return new ResponseEntity<>(eventService.getByUserId(id, pageable), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public ResponseEntity<Object> getEventsByUserAndEventId(@PathVariable("userId") Long id,
                                                            @PathVariable Long eventId) {
        return new ResponseEntity<>(eventService.getByEventIdAndUserId(eventId, id), HttpStatus.OK);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public ResponseEntity<Object> update(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @RequestBody UpdateEventDto eventUpdateDto) {
        return new ResponseEntity<>(eventService.updateUser(eventId, userId, eventUpdateDto), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> getRequestsForOwner(@PathVariable Long eventId, @PathVariable Long userId) {
        return new ResponseEntity<>(eventService.findAllRequestsByEventId(eventId, userId), HttpStatus.OK);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> patchRequestsState(@PathVariable Long userId, @PathVariable Long eventId,
                                                     @RequestBody EventRequestStatusUpdateRequest dto) {
        return new ResponseEntity<>(eventService.updateRequestsStatus(eventId, userId, dto), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/requests")
    public ResponseEntity<Object> getRequestsForUser(@PathVariable Long userId) {
        return new ResponseEntity<>(requestService.getUserRequests(userId), HttpStatus.OK);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<Object> patchRequestsStateCancel(@PathVariable Long userId,
                                                           @PathVariable Long requestId) {
        return new ResponseEntity<>(requestService.cancelRequest(userId, requestId), HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/requests")
    public ResponseEntity<Object> createRequestForEvent(@Positive @PathVariable Long userId,
                                                        @Positive @RequestParam Long eventId) {
        EventDto event = eventService.get(eventId);
        return new ResponseEntity<>(requestService.createRequest(userId, eventId, event), HttpStatus.CREATED);
    }

    @PostMapping("/users/{userId}/comments")
    public ResponseEntity<Object> createComment(@Positive @PathVariable Long userId,
                                                @RequestBody NewCommentDto newCommentDto) {
        log.info("Создание комментария к событию");
        return new ResponseEntity<>(commentService.createComment(userId, newCommentDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    public ResponseEntity<Object> deleteCommentByIdByOwner(@Positive @PathVariable Long commentId,
                                                           @PathVariable Long userId) {
        log.info("Удаление комментария автором {}", commentId);
        commentService.deleteCommentByIdByOwner(userId, commentId);
        return new ResponseEntity<>(true, HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public ResponseEntity<Object> updateComment(@PathVariable Long userId, @PathVariable Long commentId,
                                                @RequestBody UpdateCommentDto dto) {
        log.info("Обновление комментария id {} автором", commentId);
        return new ResponseEntity<>(commentService.updateCommentForEvent(commentId, userId, dto), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<Object> getCommentsByUserId(@PositiveOrZero @PathVariable Long userId) {
        log.info("Получение комментариев для пользователя id {}", userId);
        return new ResponseEntity<>(commentService.getAllForUser(userId), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/comments/{commentId}")
    public ResponseEntity<Object> getCommentByIdForUser(@PositiveOrZero @PathVariable Long userId,
                                                        @PositiveOrZero @PathVariable Long commentId) {
        log.info("Получен запрос на получение информации о комментарии {} для пользователя {}", commentId, userId);
        return new ResponseEntity<>(commentService.getCommentByIdForUser(userId, commentId), HttpStatus.OK);
    }
}
