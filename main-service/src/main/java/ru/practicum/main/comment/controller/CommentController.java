package ru.practicum.main.comment.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ru.practicum.main.comment.dto.*;
import ru.practicum.main.comment.service.CommentService;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@PathVariable @Positive Long userId,
            @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.createComment(userId, newCommentDto);
    }

    @PutMapping("{userId}/comment/{commentId}")
    public CommentDto updateComment(@PathVariable @Positive Long userId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        return commentService.updateCommentForEvent(commentId, userId, updateCommentDto);
    }

    @DeleteMapping("{userId}/comment/{commentId}")
    public void deleteCommentByOwner(@PathVariable @Positive Long userId,
            @PathVariable Long commentId) {
        commentService.deleteCommentByIdByOwner(userId, commentId);
    }

    @DeleteMapping("/admin/{commentId}")
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        commentService.deleteCommentByIdByAdmin(commentId);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentDto> getCommentsByEvent(@PathVariable Long eventId) {
        return commentService.getCommentsByEventId(eventId);
    }

    @GetMapping("/user/{userId}")
    public List<CommentDto> getUserComments(
            @PathVariable Long userId) {
        return commentService.getAllForUser(userId);
    }

    @GetMapping("{userId}/comment/{commentId}")
    public CommentDto getUserComment(@PathVariable @Positive Long userId,
            @PathVariable Long commentId) {
        return commentService.getCommentByIdForUser(userId, commentId);
    }

    @GetMapping
    public List<CommentDto> getAllComments(@RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @Positive Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return commentService.getAll(rangeStart, rangeEnd, from, size);
    }
}
