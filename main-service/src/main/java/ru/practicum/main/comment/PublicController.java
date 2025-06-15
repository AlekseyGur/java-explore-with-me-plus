package ru.practicum.main.comment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.service.CategoryService;
import ru.practicum.main.comment.service.CommentService;
import ru.practicum.main.compilations.service.CompilationService;
import ru.practicum.main.event.dto.EventFilter;
import ru.practicum.main.event.service.EventService;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping
public class PublicController {
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CommentService commentService;
    private final CompilationService compilationService;

    @GetMapping("/categories/{catId}")
    public ResponseEntity<Object> getCategory(@PathVariable("catId") Long id) {
        log.info("Получение информации о категории id {}", id);
        return new ResponseEntity<>(categoryService.get(id), HttpStatus.OK);
    }

    @GetMapping("/categories")
    public ResponseEntity<Object> getCategories(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получение списка категорий с {} по {}", from, from + size);
        return new ResponseEntity<>(categoryService.findAll(from, size), HttpStatus.OK);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<Object> getEventById(@Positive @PathVariable Long id, HttpServletRequest request) {
        log.info("Получение события по id: {} (path: {})", id, request.getRequestURI());
        return new ResponseEntity<>(eventService.get(id, request), HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<Object> getEvents(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(required = false, defaultValue = "EVENT_DATE") FilterSort sort,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                            @Positive @RequestParam(defaultValue = "10") Integer size,
                                            HttpServletRequest request) {
        log.info("Фильтр событий (path: {})", request.getRequestURI());
        return new ResponseEntity<>(eventService.getByFilter(
                new EventFilter(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort.name(), from, size), request
        ), HttpStatus.OK);
    }

    @GetMapping("/compilations")
    public ResponseEntity<Object> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение всех подборок, фильтр pinned = {}", pinned);
        return new ResponseEntity<>(compilationService.getCompilationList(pinned, from, size), HttpStatus.OK);
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<Object> getCompilationById(@PositiveOrZero @PathVariable Long compId) {
        log.info("Получение информации о подборке id {}", compId);
        return new ResponseEntity<>(compilationService.getById(compId), HttpStatus.OK);
    }

    @GetMapping("/comments/events/{eventId}")
    public ResponseEntity<Object> getCommentsByEvent(@PositiveOrZero @PathVariable Long eventId) {
        log.info("Получение комментариев для события id {}", eventId);
        return new ResponseEntity<>(commentService.getCommentsByEventId(eventId), HttpStatus.OK);
    }

    public enum FilterSort {
        EVENT_DATE, VIEWS
    }
}

