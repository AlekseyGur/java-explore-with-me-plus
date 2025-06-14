package ru.practicum.main.user.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.practicum.main.system.exception.DuplicatedDataException;
import ru.practicum.main.system.exception.NotFoundException;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.dto.UserNewDto;
import ru.practicum.main.user.dto.UserUpdateDto;
import ru.practicum.main.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto get(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
    }

    @Override
    public List<UserDto> get(List<Long> ids) {
        return userRepository.findByIdIn(ids).stream().map(UserMapper::toDto).toList();
    }

    @Override
    public Page<UserDto> get(List<Long> ids, Pageable pageable) {
        if (ids == null || ids.isEmpty()) {
            return UserMapper.toDto(userRepository.findAll(pageable));
        }
        return UserMapper.toDto(userRepository.findByIdIn(ids, pageable));
    }

    @Override
    @Transactional
    public UserDto create(UserNewDto userDto) {
        User user = UserMapper.fromDto(userDto);

        if (checkEmailExist(user.getEmail())) {
            throw new DuplicatedDataException("Пользователь с таким email уже существует");
        }

        if (user.getName() != null && user.getName().isBlank()) {
            user.setName("Unnamed");
        }

        User userSaved = userRepository.save(user);
        userRepository.flush();
        return UserMapper.toDto(userSaved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
        userRepository.flush();
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean checkEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserDto patch(UserUpdateDto userDto) {
        User user = UserMapper.fromDto(userDto);

        if (!userRepository.existsById(user.getId())) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }

        if (user.getEmail() != null && checkEmailExist(user.getEmail())) {
            throw new DuplicatedDataException("Пользователь с таким email уже существует");
        }

        Long id = user.getId();
        User userSaved = userRepository.findById(id).get();

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            userSaved.setEmail(user.getEmail());
        }

        if (user.getName() != null && !user.getName().isBlank()) {
            userSaved.setName(user.getName());
        }

        User userLoad = userRepository.save(userSaved);
        userRepository.flush();
        return UserMapper.toDto(userLoad);
    }
}