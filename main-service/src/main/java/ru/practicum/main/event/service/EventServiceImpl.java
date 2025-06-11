package ru.practicum.main.event.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceUnit;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.category.service.CategoryService;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.*;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.location.service.LocationService;
import ru.practicum.main.location.dto.LocationDto;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.enums.RequestStatus;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.request.service.RequestService;
import ru.practicum.main.system.exception.NotFoundException;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.repository.UserRepository;
import ru.practicum.main.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final RequestService requestService;

    @Override
    public EventDto get(Long eventId) {
        EventDto event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED.toString())
                .map(EventMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + eventId + " was not found"));
        return event;
    }

    @Override
    public List<EventDto> get(List<Long> eventIds) {
        List<EventDto> events = eventRepository.findByIdInAndState(eventIds, EventState.PUBLISHED.toString()).stream()
                .map(EventMapper::toDto)
                .toList();
        return events;
    }

    @Override
    public List<EventShortDto> getByFilter(EventFilter filter) {
        Specification<Event> spec = Specification.where(hasText(filter.getText()));

        if (!filter.getText().isEmpty()) {
            spec = spec.and(hasText(filter.getText()));
        }

        if (filter.getCategories() != null) {
            spec = spec.and(hasCategories(filter.getCategories()));
        }

        if (filter.getPaid() != null) {
            spec = spec.and(hasPaid(filter.getPaid()));
        }

        if (filter.getRangeStart() != null) {
            spec = spec.and(hasRangeStart(filter.getRangeStart()));
        }
        if (filter.getRangeEnd() != null) {
            spec = spec.and(hasRangeEnd(filter.getRangeEnd()));
        }

        if (filter.getOnlyAvailable() != null) {
            spec = spec.and(hasAvailable(filter.getOnlyAvailable()));
        }

        Sort sort = Sort.unsorted();
        if ("EVENT_DATE".equals(filter.getSort())) {
            sort = Sort.by("eventDate").ascending();
        } else if ("VIEWS".equals(filter.getSort())) {
            sort = Sort.by("views").descending();
        }

        Pageable pageable = PageRequest.of(
                filter.getFrom(),
                filter.getSize(),
                sort);

        return addInfo(eventRepository.findAll(spec, pageable));
    }

    private Specification<Event> hasText(String text) {
        return (root, query, cb) -> cb.like(root.get("name"), "%" + text + "%");
    }

    private Specification<Event> hasCategories(Collection<Long> categories) {
        return (root, query, cb) -> root.get("category").in(categories);
    }

    private Specification<Event> hasPaid(Boolean paid) {
        return (root, query, cb) -> cb.equal(root.get("paid"), paid);
    }

    private Specification<Event> hasRangeStart(LocalDateTime start) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("eventDate"), start);
    }

    private Specification<Event> hasRangeEnd(LocalDateTime end) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("eventDate"), end);
    }

    private Specification<Event> hasAvailable(Boolean available) {
        return (root, query, cb) -> cb.equal(root.get("available"), available);
    }

    private List<EventShortDto> addInfo(List<Event> events) {
        List<Long> eventsIds = events.stream().map(Event::getId).toList();
        List<Long> categoryIds = events.stream().map(Event::getCategoryId).toList();
        List<Long> usersIds = events.stream().map(Event::getInitiatorId).toList();

        List<CategoryDto> categories = categoryService.get(categoryIds);
        List<UserDto> users = userService.get(usersIds);
        Map<Long, Integer> requests = requestService.getConfirmedEventsRequestsCount(eventsIds);

        Map<Long, CategoryDto> catsByIds = categories.stream()
                .collect(Collectors.toMap(CategoryDto::getId, Function.identity()));

        Map<Long, UserDto> usersByIds = users.stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));

        return events.stream()
                .map(EventMapper::toShortDto)
                .map(x -> {
                    x.setConfirmedRequests(requests.getOrDefault(x.getId(), 0));
                    x.setCategory(catsByIds.getOrDefault(x.getCategoryId(), null));
                    x.setInitiator(usersByIds.getOrDefault(x.getInitiatorId(), null));
                    return x;
                })
                .toList();
    }


}
