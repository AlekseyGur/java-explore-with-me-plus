package ru.practicum.main.user.service;

import java.util.List;

import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.dto.UserNewDto;
import ru.practicum.main.user.dto.UserUpdateDto;

public interface UserService {
    UserDto get(Long id);

    List<UserDto> get(List<Long> id);

    UserDto create(UserNewDto user);

    void delete(Long id);

    boolean existsById(Long id);

    boolean checkEmailExist(String email);

    UserDto patch(UserUpdateDto user);
}