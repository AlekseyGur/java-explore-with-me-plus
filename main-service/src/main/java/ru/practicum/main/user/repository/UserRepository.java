package ru.practicum.main.user.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import ru.practicum.main.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    List<User> findByIdIn(List<Long> userIds);

    @NonNull
    Page<User> findAll(@NonNull Pageable pageable);

    Page<User> findByIdIn(List<Long> ids, Pageable pageable);
}