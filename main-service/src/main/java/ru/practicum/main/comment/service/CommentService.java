package ru.practicum.main.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.mappers.CommentMapper;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.RequestValidationException;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;
import ru.practicum.main.validation.DtoValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final DtoValidator validator;

    public CommentDto createComment(Long userId, NewCommentDto newCommentDto) {
        LocalDateTime now = LocalDateTime.now();
        log.info("Создание комментария пользователем {}", userId);
        User commentator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден",
                        "Запрашиваемый объект не найден или не доступен", now));
        validator.validateNewCommentDto(newCommentDto);
        Event stored = eventRepository.findById(newCommentDto.getEventId()).orElseThrow(() ->
                new NotFoundException("Событие с id " + newCommentDto.getEventId() + " не найдено",
                        "Запрашиваемый объект не найден или не доступен", now));
        if (!stored.getState().equals(EventState.PUBLISHED)) {
            throw new RequestValidationException(
                    "Событие не опубликовано",
                    "Можно комментировать только опубликованные события",
                    now);
        }
        Comment newComment = new Comment(null, newCommentDto.getText(), commentator, stored, now);
        return CommentMapper.INSTANCE.toCommentDto(commentRepository.save(newComment));
    }

    public void deleteCommentByIdByOwner(Long userId, Long commentId) {
        LocalDateTime now = LocalDateTime.now();
        log.info("Удаление комментария {} автором {}", commentId, userId);
        User commentator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден",
                        "Запрашиваемый объект не найден или не доступен", now));
        Comment stored = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с id " + commentId + " не найден",
                        "Запрашиваемый объект не найден или не доступен", now));
        if (!Objects.equals(commentator.getId(), stored.getUser().getId())) {
            throw new NotFoundException("Удалять комментарий может только автор или администратор",
                    "Пользователь не автор", now);
        }
        commentRepository.deleteById(commentId);
    }

    public void deleteCommentByIdByAdmin(Long commentId) {
        LocalDateTime now = LocalDateTime.now();
        log.info("Удаление комментария администратором: {}", commentId);
        commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с id " + commentId + " не найден",
                        "Запрашиваемый объект не найден или не доступен", now));
        commentRepository.deleteById(commentId);
    }

    public CommentDto updateCommentForEvent(Long commentId, Long userId, UpdateCommentDto dto) {
        LocalDateTime now = LocalDateTime.now();
        log.info("Обновление комментария {} пользователем {}", commentId, userId);
        User commentator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден",
                        "Запрашиваемый объект не найден или не доступен", now));
        Comment stored = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с id " + commentId + " не найден",
                        "Запрашиваемый объект не найден или не доступен", now));
        if (!Objects.equals(commentator.getId(), stored.getUser().getId())) {
            throw new NotFoundException("Обновлять комментарий может только автор",
                    "Пользователь не автор", now);
        }
        validator.validateUpdCommentDto(dto);
        Comment updComment = CommentMapper.INSTANCE.updateComment(dto, stored);
        return CommentMapper.INSTANCE.toCommentDto(commentRepository.save(updComment));
    }

    public List<CommentDto> getCommentsByEventId(Long eventId) {
        log.info("Получение комментариев для события {}", eventId);
        return commentRepository.getAllByEventId(eventId)
                .stream()
                .map(CommentMapper.INSTANCE::toCommentDto)
                .collect(Collectors.toList());
    }

    public List<CommentDto> getAllForUser(Long userId) {
        log.info("Получение всех комментариев пользователя {}", userId);
        return commentRepository.getAllByUserId(userId)
                .stream()
                .map(CommentMapper.INSTANCE::toCommentDto)
                .collect(Collectors.toList());
    }

    public CommentDto getCommentByIdForUser(Long userId, Long commentId) {
        LocalDateTime now = LocalDateTime.now();
        log.info("Получение комментария {} для пользователя {}", commentId, userId);
        User commentator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден",
                        "Запрашиваемый объект не найден или не доступен", now));
        Comment stored = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с id " + commentId + " не найден",
                        "Запрашиваемый объект не найден или не доступен", now));
        if (!Objects.equals(commentator.getId(), stored.getUser().getId())) {
            throw new NotFoundException("Информацию о комментарии может получить только автор",
                    "Пользователь не автор", now);
        }
        return CommentMapper.INSTANCE.toCommentDto(stored);
    }

    public List<CommentDto> getAll(LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "createdOn"));

        List<Comment> comments = commentRepository.findAllByCreatedOnBetween(rangeStart, rangeEnd, pageable);

        return comments.stream()
                .map(CommentMapper.INSTANCE::toCommentDto)
                .collect(Collectors.toList());
    }
}
