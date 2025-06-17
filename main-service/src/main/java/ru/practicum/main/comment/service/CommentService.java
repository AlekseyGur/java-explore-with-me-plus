package ru.practicum.main.comment.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.mappers.CommentMapper;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.system.exception.AccessDeniedException;
import ru.practicum.main.system.exception.NotFoundException;
import ru.practicum.main.system.exception.ValidationException;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final EventService eventService;
    private final UserService userService;

    public CommentDto get(Long id) {
        return commentRepository.findById(id)
                .map(CommentMapper::toCommentDto)
                .orElseThrow(() -> new NotFoundException("Комментарий с таким id не найден"));
    }

    public List<CommentDto> getCommentsByEventId(Long eventId) {
        return commentRepository.getAllByEventId(eventId)
                .stream().map(CommentMapper::toCommentDto).toList();
    }

    public List<CommentDto> getAllForUser(Long userId) {
        return commentRepository.getAllByUserId(userId)
                .stream().map(CommentMapper::toCommentDto).toList();
    }

    @Transactional
    public CommentDto createComment(Long userId, NewCommentDto newCommentDto) {
        UserDto user = userService.get(userId);
        EventDto event = eventService.get(newCommentDto.getEventId());

        if (!event.getState().equals(EventState.PUBLISHED.toString())) {
            throw new ValidationException("Можно комментировать только опубликованные события");
        }

        User nUser = new User();
        nUser.setId(user.getId());

        Event nEvent = new Event();
        nEvent.setId(event.getId());

        Comment newComment = new Comment();
        newComment.setCreatedOn(LocalDateTime.now());
        newComment.setUser(nUser);
        newComment.setEvent(nEvent);
        newComment.setText(newCommentDto.getText());
        return CommentMapper.toCommentDto(commentRepository.save(newComment));
    }

    @Transactional
    public void deleteCommentByIdByOwner(Long userId, Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Комментарий с таким id не найден");
        }

        CommentDto stored = getDtoOrThrow(commentId);
        UserDto user = userService.get(userId);

        if (!user.getId().equals(stored.getUserId())) {
            throw new AccessDeniedException("Удалять комментарий может только автор или администратор");
        }
        commentRepository.deleteById(commentId);
        commentRepository.flush();
    }

    @Transactional
    public void deleteCommentByIdByAdmin(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Комментарий с таким id не найден");
        }
        commentRepository.deleteById(commentId);
        commentRepository.flush();
    }

    @Transactional
    public CommentDto updateCommentForEvent(Long commentId, Long userId, UpdateCommentDto dto) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Комментарий с таким id не найден");
        }

        Comment stored = commentRepository.findById(commentId).get();
        UserDto user = userService.get(userId);

        if (!user.getId().equals(stored.getUser().getId())) {
            throw new AccessDeniedException("Обновлять комментарий может только автор или администратор");
        }

        if (dto.getText() != null) {
            stored.setText(dto.getText());

            User nUser = new User();
            nUser.setId(user.getId());

            Event nEvent = new Event();
            nEvent.setId(stored.getEvent().getId());
            commentRepository.save(stored);
        }
        return get(commentId);
    }

    public List<CommentDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "createdOn"));

        Page<Comment> comments = commentRepository.findAll(pageable);

        return comments.stream().map(CommentMapper::toCommentDto).toList();
    }

    private CommentDto getDtoOrThrow(Long id) {
        return commentRepository.findById(id)
                .map(CommentMapper::toCommentDto)
                .orElseThrow(() -> new NotFoundException("Комментарий с таким id не найден"));
    }
}
