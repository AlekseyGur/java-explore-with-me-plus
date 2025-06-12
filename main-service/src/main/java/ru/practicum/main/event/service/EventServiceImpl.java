package ru.practicum.main.event.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.service.CategoryService;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.dto.EventFilter;
import ru.practicum.main.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.event.dto.EventSearchParameters;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.model.StateAction;
import ru.practicum.main.event.model.StateActionUser;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.location.dto.LocationDto;
import ru.practicum.main.location.service.LocationService;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.enums.RequestStatus;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.system.exception.AccessDeniedException;
import ru.practicum.main.system.exception.ConstraintViolationException;
import ru.practicum.main.system.exception.NotFoundException;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.service.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
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
    @Transactional
    public EventDto update(Long eventId, Long userId, UpdateEventUserRequest updated) {
        if (!updated.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
            throw new ConstraintViolationException("Событие можно запланировать минимум за один час до его начала");
        }

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Собатиые не найдено"));

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
        Map<Long, Integer> requests = requestRepository.getCountByEventIdInAndStatus(
                eventsIds,
                RequestStatus.CONFIRMED.toString()).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue().intValue()));

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

    // Ниже ещё не проверено

    @Override
    public List<EventDto> search(EventSearchParameters parameters) {
        BooleanExpression expression = QEvent.event.id.gt(parameters.from());

        if (parameters.usersIds() != null) {
            expression = expression.and(QEvent.event.initiator.id.in(parameters.usersIds()));
        }

        if (parameters.eventStates() != null) {
            expression = expression.and(QEvent.event.state.in(parameters.eventStates()));
        }

        if (parameters.categoriesIds() != null) {
            expression = expression.and(QEvent.event.category.id.in(parameters.categoriesIds()));
        }

        if (parameters.rangeStart() != null && parameters.rangeEnd() != null) {
            if (parameters.rangeStart().isAfter(parameters.rangeEnd())) {
                throw new InvalidDateException("Время начала диапазона поиска позже времени завершения");
            }

            expression = expression.and(QEvent.event.eventDate.between(parameters.rangeStart(), parameters.rangeEnd()));
        } else if (parameters.rangeStart() != null) {
            expression = expression.and(QEvent.event.eventDate.after(parameters.rangeStart()));
        } else if (parameters.rangeEnd() != null) {
            expression = expression.and(QEvent.event.eventDate.before(parameters.rangeEnd()));
        }

        Page<Event> result;
        Pageable pageable = PageRequest.of(0, parameters.size());
        result = eventRepository.findAll(expression, pageable);

        return result.stream()
                .map(MapperEvent::toEventDto)
                .toList();
    }

    //

    @Override
    @Transactional
    public EventDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("События с id = " + eventId + " не найдено"));

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new InvalidDateException(
                    "Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryService.findCategoryById(updateEventAdminRequest.getCategory());
            event.setCategory(category);
        }

        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }

        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(updateEventAdminRequest.getLocation());
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            StateAction stateAction = updateEventAdminRequest.getStateAction();

            if (stateAction == StateAction.PUBLISH_EVENT) {
                if (event.getState() != EventState.PENDING) {
                    throw new InvalidStateException(
                            "Событие можно публиковать, только если оно в состоянии ожидания публикации");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }

            if (stateAction == StateAction.REJECT_EVENT) {
                if (event.getState() == EventState.PUBLISHED) {
                    throw new InvalidStateException("Событие можно отклонить, только если оно еще не опубликовано");
                }

                event.setState(EventState.CANCELED);
            }
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        return MapperEvent.toEventDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> findRequestsByEventId(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with was not found " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("События с id = " + eventId + " не найдено"));

        return participationRequestRepository.findAllByEvent(event).stream()
                .map(ParticipationRequestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsStatus(long userId, long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not found with id = " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event is not found with id = " + eventId));
        if (!event.getInitiator().equals(user))
            throw new ForbiddenException(
                    "User with id = " + userId + " is not a initiator of event with id = " + eventId);

        // если для события лимит заявок равен 0 или отключена пре-модерация заявок, то
        // подтверждение заявок не требуется
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return result;
        }

        Collection<ParticipationRequest> requests = participationRequestRepository
                .findByEventIdAndIdIn(eventId, eventRequestStatusUpdateRequest.getRequestIds()).stream().toList();

        if (event.getConfirmedRequests() + requests.size() > event.getParticipantLimit()
                && eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ConflictException("exceeding the limit of participants");
        }

        if (requests.stream().anyMatch(request -> request.getStatus().equals(RequestStatus.CONFIRMED)
                && eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED))) {
            throw new ConflictException("request already confirmed");
        }
        for (ParticipationRequest oneRequest : requests) {
            oneRequest.setStatus(RequestStatus.valueOf(eventRequestStatusUpdateRequest.getStatus().toString()));
        }
        participationRequestRepository.saveAll(requests);
        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + requests.size());
        }
        eventRepository.save(event);
        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
            result.setConfirmedRequests(requests.stream()
                    .map(ParticipationRequestMapper::toDto).toList());
        }

        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED)) {
            result.setRejectedRequests(requests.stream()
                    .map(ParticipationRequestMapper::toDto).toList());
        }

        return result;
    }


    @Override
    public Collection<ParticipationRequestDto> findAllRequestsByEventId(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not found with id = " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event is not found with id = " + eventId));
        Collection<ParticipationRequestDto> result = new ArrayList<>();
        result = participationRequestRepository.findAllByEvent(event).stream()
                .map(ParticipationRequestMapper::toDto).toList();
        return result;
    }
}
