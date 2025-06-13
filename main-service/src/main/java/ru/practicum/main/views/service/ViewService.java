package ru.practicum.main.views.service;

import ru.practicum.main.views.dto.ViewStatDto;

public interface ViewService {
    void add(Long eventId, String ip);

    ViewStatDto stat(Long eventId);
}
