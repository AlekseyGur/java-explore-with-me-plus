package ru.practicum.main.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.main.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    List<User> getByIdIn(List<Long> userIds);
}