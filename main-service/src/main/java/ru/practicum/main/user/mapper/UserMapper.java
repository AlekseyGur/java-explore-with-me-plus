package ru.practicum.main.user.mapper;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import lombok.experimental.UtilityClass;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.dto.UserNewDto;
import ru.practicum.main.user.dto.UserShortDto;
import ru.practicum.main.user.dto.UserUpdateDto;

@UtilityClass
public class UserMapper {
    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static User fromDto(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static UserShortDto toShortfromDto(UserDto userDto) {
        UserShortDto user = new UserShortDto();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        return user;
    }

    public static User fromDto(UserNewDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static User fromDto(UserUpdateDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static List<User> fromDto(List<UserDto> usersDto) {
        return usersDto.stream().map(UserMapper::fromDto).toList();
    }

    public static List<UserDto> toDto(List<User> users) {
        return users.stream().map(UserMapper::toDto).toList();
    }

    public static Page<UserDto> toDto(Page<User> users) {
        List<UserDto> userDtos = users.stream()
                .map(UserMapper::toDto).toList();

        return new PageImpl<>(userDtos, users.getPageable(), users.getTotalElements());
    }
}