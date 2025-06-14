package ru.practicum.main.comment.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapping;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.comment.model.Comment;

@Mapper(componentModel = "spring",
        uses = {
                UserMapper.class,
                EventMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "userId", source = "user.id")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "event", ignore = true)
    Comment updateComment(UpdateCommentDto updateCommentDto, @MappingTarget Comment storedComment);
}