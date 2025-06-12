package ru.practicum.main.event.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.service.CategoryService;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.dto.EventFilter;
import ru.practicum.main.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventDto;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.location.dto.LocationDto;
import ru.practicum.main.location.service.LocationService;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.enums.RequestStatus;
import ru.practicum.main.request.service.RequestService;
import ru.practicum.main.system.exception.AccessDeniedException;
import ru.practicum.main.system.exception.ConditionsNotMetException;
import ru.practicum.main.system.exception.ConstraintViolationException;
import ru.practicum.main.system.exception.NotFoundException;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.service.UserService;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final RequestService requestService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;

    @Override
    public EventDto get(Long eventId) {
        EventDto event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED.toString())
                .map(this::addInfo)
                .orElseThrow(() -> new NotFoundException("Событие с таким id не найдено"));
        return event;
    }

    @Override
    public List<EventDto> get(List<Long> eventIds) {
        return addInfo(eventRepository.findByIdInAndState(eventIds, EventState.PUBLISHED.toString()));
    }

    @Override
    public Page<EventShortDto> getByUserId(Long userId, Pageable pageable) {
        Page<Event> page = eventRepository.findAllByInitiatorId(userId, pageable);
        return new PageImpl<>(
                EventMapper.toShortDtoFromDto(addInfo(page.getContent())),
                pageable,
                page.getTotalElements());
    }

    @Override
    public EventDto getByEventIdAndUserId(Long eventId, Long userId) {
        return EventMapper.toDto(eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с таким id не найдено ")));
    }

    @Override
    @Transactional
    public EventDto create(Long userId, NewEventDto newEventDto) {
        if (!newEventDto.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
            throw new ConstraintViolationException("Событие можно запланировать минимум за один час до его начала");
        }

        if (!userService.existsById(userId)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }

        if (!categoryService.existsById(newEventDto.getCategory())) {
            throw new NotFoundException("Категории с таким id не существует");
        }

        Event event = EventMapper.fromNew(newEventDto);
        event.setInitiatorId(userId);
        event.setCategoryId(newEventDto.getCategory());
        event.setLocationId(locationService.create(newEventDto.getLocation()).getId());
        return addInfo(eventRepository.save(event));
    }

    @Override
    public boolean existsById(Long id) {
        return eventRepository.existsById(id);
    }

    @Override
    public boolean existsByIdAndInitiatorId(Long eventId, Long userId) {
        return eventRepository.existsByIdAndInitiatorId(eventId, userId);
    }

    @Override
    @Transactional
    public void increaseViews(Long id) {
        if (!existsById(id)) {
            throw new NotFoundException("Собатиые не найдено");
        }
        eventRepository.increaseViews(id);
    }

    @Override
    public Page<EventShortDto> getByFilter(EventFilter filter) {
        EventSpecs e = new EventSpecs();
        Specification<Event> spec = Specification.where(null);

        if (filter.getCategories() != null) {
            spec = spec.and(e.hasText(filter.getText()));
        }

        if (filter.getCategories() != null) {
            spec = spec.and(e.hasCategories(filter.getCategories()));
        }

        if (filter.getPaid() != null) {
            spec = spec.and(e.hasPaid(filter.getPaid()));
        }

        if (filter.getRangeStart() != null) {
            spec = spec.and(e.hasRangeStart(filter.getRangeStart()));
        }
        if (filter.getRangeEnd() != null) {
            spec = spec.and(e.hasRangeEnd(filter.getRangeEnd()));
        }

        if (filter.getOnlyAvailable() != null) {
            spec = spec.and(e.hasAvailable(filter.getOnlyAvailable()));
        }

        if (filter.getIsDtoForAdminApi()) {
            if (filter.getStates() != null) {
                spec = spec.and(e.hasStates(filter.getStates()));
            }

            if (filter.getUsers() != null) {
                spec = spec.and(e.hasUsers(filter.getUsers()));
            }
        }

        Sort sort = Sort.unsorted();
        if ("EVENT_DATE".equals(filter.getSort())) {
            sort = Sort.by("eventDate").ascending();
        } else {
            sort = Sort.by("views").descending();
        }

        Pageable pageable = PageRequest.of(
                filter.getFrom(),
                filter.getSize(),
                sort);

        Page<Event> page = eventRepository.findAll(spec, pageable);
        return new PageImpl<>(
                EventMapper.toShortDtoFromDto(addInfo(page.getContent())),
                pageable,
                page.getTotalElements());
    }

    @Override
    public EventDto updateAdmin(Long eventId, UpdateEventDto updated) {
        return update(eventId, null, updated);
    }

    @Override
    public EventDto updateUser(Long eventId, Long userId, UpdateEventDto updated) {
        return update(eventId, userId, updated);
    }

    @Transactional
    public EventDto update(Long eventId, Long userId, UpdateEventDto updated) {
        if (!updated.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
            throw new ConstraintViolationException("Событие можно запланировать минимум за один час до его начала");
        }

        Event event;
        if (userId != null) {
            event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Собатиые не найдено"));
        } else {
            event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException("Собатиые не найдено"));
        }

        if (event.getState().equals(EventState.PUBLISHED.toString())) {
            throw new AccessDeniedException("Невозможно имзенить опубликованное событие");
        }

        if (event.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
            throw new ConstraintViolationException("Событие можно изменять минимум за один час до его начала");
        }

        if (updated.getCategory() != null) {
            if (!categoryService.existsById(updated.getCategory())) {
                throw new NotFoundException("Категория не найдена");
            }
            event.setCategoryId(updated.getCategory());
        }

        if (updated.getLocation() != null) {
            LocationDto location = locationService.getByLonLat(
                    updated.getLocation().getLon(),
                    updated.getLocation().getLat());

            event.setLocationId(location.getId());
        }

        return addInfo(eventRepository.save(event));
    }

    private List<EventDto> addInfo(List<Event> events) {
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
                .map(EventMapper::toDto)
                .map(x -> {
                    x.setConfirmedRequests(requests.getOrDefault(x.getId(), 0));
                    x.setCategory(catsByIds.getOrDefault(x.getCategoryId(), null));
                    x.setInitiator(usersByIds.getOrDefault(x.getInitiatorId(), null));
                    return x;
                })
                .toList();
    }

    private EventDto addInfo(Event event) {
        return addInfo(List.of(event)).get(0);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsStatus(Long eventId, Long userId,
            EventRequestStatusUpdateRequest updateRequest) {

        UserDto user = userService.get(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с таким id не найдено"));

        if (!event.getInitiatorId().equals(user.getId()))
            throw new AccessDeniedException(
                    "Пользователь может изменять статус только событиям, которые он сделал сам");

        // если для события лимит заявок равен 0 или отключена пре-модерация заявок, то
        // подтверждение заявок не требуется
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return result;
        }

        List<Long> requestsIds = updateRequest.getRequestIds();
        List<ParticipationRequestDto> requests = requestService
                .findByEventIdAndIdIn(eventId, requestsIds);

        if (requests.stream().anyMatch(x -> x.getStatus().equals(RequestStatus.REJECTED))) {
            throw new ConditionsNotMetException(
                    "Статус можно изменить только у заявок, находящихся в состоянии ожидания");
        }

        List<ParticipationRequestDto> requestsConfirmed = requests.stream()
                .filter(x -> x.getStatus().equals(RequestStatus.CONFIRMED))
                .toList();

        List<ParticipationRequestDto> requestsUnconfirmed = requests.stream()
                .filter(x -> x.getStatus().equals(RequestStatus.PENDING))
                .toList();

        int availableSlots = (int) (event.getParticipantLimit() - requestsConfirmed.size());
        if (availableSlots < 1L) {
            throw new ConditionsNotMetException(
                    "Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие");
        }
        if (availableSlots > requestsUnconfirmed.size()) {
            availableSlots = requestsUnconfirmed.size();
        }

        List<ParticipationRequestDto> toConfirm = requestsUnconfirmed.subList(
                0, availableSlots);

        List<Long> toConfirmIds = toConfirm.stream()
                .map(ParticipationRequestDto::getId).toList();

        requestService.setStatusAll(toConfirmIds, RequestStatus.CONFIRMED.toString());

        List<ParticipationRequestDto> toReject = List.of();
        if (availableSlots < requestsUnconfirmed.size()) {
            toReject = requestsUnconfirmed.subList(
                    availableSlots, requestsUnconfirmed.size());

            List<Long> toRejectIds = toConfirm.stream()
                    .map(ParticipationRequestDto::getId).toList();

            requestService.setStatusAll(toRejectIds, RequestStatus.REJECTED.toString());
        }

        requestsConfirmed.addAll(toConfirm);
        result.setConfirmedRequests(requestsConfirmed);
        result.setRejectedRequests(toReject);
        return result;
    }

    @Override
    public List<ParticipationRequestDto> findAllRequestsByEventId(Long eventId, Long userId) {

        if (!userService.existsById(userId)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("События с таким id не существует");
        }

        return requestService.getByEventId(eventId);
    }

}
