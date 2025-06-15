package ru.practicum.main.user.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.dto.UserNewDto;
import ru.practicum.main.user.dto.UserUpdateDto;

public interface UserService {
    UserDto get(Long id);

    Page<UserDto> get(List<Long> ids, Pageable pageable);

    List<UserDto> get(List<Long> ids);

    UserDto create(UserNewDto user);

    void delete(Long id);

    boolean existsById(Long id);

    boolean checkEmailExist(String email);

    UserDto patch(UserUpdateDto user);
}