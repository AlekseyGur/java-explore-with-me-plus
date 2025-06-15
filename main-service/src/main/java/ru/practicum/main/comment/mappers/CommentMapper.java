package ru.practicum.main.comment.mappers;

import java.time.LocalDateTime;

import lombok.experimental.UtilityClass;

import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.user.model.User;
import ru.practicum.main.comment.model.Comment;

@UtilityClass
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setUserId(comment.getUser().getId());
        dto.setEventId(comment.getEvent().getId());
        dto.setCreatedOn(comment.getCreatedOn());

        return dto;
    }

    public static Comment updateComment(UpdateCommentDto updateCommentDto, Comment storedComment) {
        if (updateCommentDto == null || storedComment == null) {
            return null;
        }

        if (updateCommentDto.getText() != null) {
            storedComment.setText(updateCommentDto.getText());
        }

        return storedComment;
    }

    public static Comment fromNewCommentDto(NewCommentDto newCommentDto, User user, Event event) {
        if (newCommentDto == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setUser(user);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());

        return comment;
    }
}
