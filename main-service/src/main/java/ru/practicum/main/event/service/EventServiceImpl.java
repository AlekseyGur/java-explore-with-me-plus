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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.category.service.CategoryService;
import ru.practicum.main.event.dto.*;
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
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.repository.UserRepository;
import ru.practicum.main.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
    public EventDto getById(long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED.toString())
                .orElseThrow(() -> new EntityNotFoundException("Event with id=" + eventId + " was not found"));
        return MapperEvent.toDto(event);
    }

    @Override
    public Collection<EventShortDto> getEventsByFilter(EventPublicParam param) {
        Collection<Event> events = new ArrayList<>();
        QEvent event = QEvent.event;
        BooleanExpression expression = event.state.eq(EventState.PUBLISHED);

        if (Objects.nonNull(param.getRangeStart()) && Objects.nonNull(param.getRangeEnd())) {
            expression = expression.and(event.eventDate.between(param.getRangeStart(), param.getRangeEnd()));
        } else {
            expression = expression.and(event.eventDate.after(LocalDateTime.now()));
        }
        if (Objects.nonNull(param.getText())) {
            expression = expression.and(event.description.containsIgnoreCase(param.getText()))
                    .or(event.annotation.containsIgnoreCase(param.getText()));
        }
        if (Objects.nonNull(param.getPaid())) {
            expression = expression.and(event.paid.eq(param.getPaid()));
        }
        if (Objects.nonNull(param.getCategories())) {
            expression = expression.and(event.category.id.in(param.getCategories()));
        }
        if (Objects.nonNull(param.getOnlyAvailable()) && param.getOnlyAvailable()) {
            expression = expression.and(event.confirmedRequests.lt(event.participantLimit));
        }
        JPAQuery<Event> query = queryFactory.selectFrom(event)
                .where(expression)
                .offset(param.getFrom())
                .limit(param.getSize());

        if (Objects.nonNull(param.getSort())) {
            if (param.getSort().equals("VIEWS")) {
                events = query.orderBy(event.views.asc()).fetch();
            }
            if (param.getSort().equals("EVENT_DATE")) {
                events = query.orderBy(event.eventDate.asc()).fetch();
            }
        } else {
            events = query.fetch();
        }

        return events.stream()
                .map(MapperEvent::toEventShortDto)
                .toList();
    }

    @Override
    @Transactional
    public void changeViews(Long id) {
        Event event = eventRepository.findById(id).get();
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
    }

    @Override
    @Transactional
    public EventDto create(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate() != null
                && !newEventDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new EventDateValidationException("Event date 2+ hours after now");
        }
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category was not found " + newEventDto.getCategory()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with was not found " + userId));
        Event event = MapperEvent.fromDto(newEventDto, category, user);
        event.setLocation(locationRepository.create(newEventDto.getLocation()));

        if (newEventDto.getPaid() == null) {
            event.setPaid(false);
        }
        if (newEventDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0L);
        }
        if (newEventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        event.setConfirmedRequests(0L);
        event.setCreatedOn(LocalDateTime.now());
        return MapperEvent.toDto(eventRepository.save(event));

    }

    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, page).stream()
                .map(MapperEvent::toEventShortDto).toList();
    }

    @Override
    public EventDto getEventById(Long userId, Integer eventId) {
        return MapperEvent.toDto(eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found " + eventId)));
    }

    @Override
    @Transactional
    public EventDto updateEvent(Long userId, Integer eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found " + eventId));

        validateForPrivate(event.getState(), updateEventUserRequest.getStateAction());
        Category category;
        if (updateEventUserRequest.getCategory() != null) {
            category = categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Event not found " + eventId));
        } else {
            category = null;
        }
        Location location;
        if (updateEventUserRequest.getLocation() != null) {
            location = locationRepository.create(updateEventUserRequest.getLocation());
        } else {
            location = null;
        }

        if (updateEventUserRequest.getEventDate() != null
                && !updateEventUserRequest.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new EventDateValidationException("Event date 2+ hours after now");
        }

        MapperEvent.updateFromDto(event, updateEventUserRequest, category, location);
        return MapperEvent.toDto(eventRepository.save(event));
    }

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
                .map(MapperEvent::toDto)
                .toList();
    }

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

        return MapperEvent.toDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> findRequestsByEventId(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with was not found " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("События с id = " + eventId + " не найдено"));

        return participationRequestRepository.findAllByEventId(event).stream()
                .map(RequestMapper::toDto)
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
                    .map(RequestMapper::toDto).toList());
        }

        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED)) {
            result.setRejectedRequests(requests.stream()
                    .map(RequestMapper::toDto).toList());
        }

        return result;
    }

    private void validateForPrivate(EventState eventState, StateActionUser stateActionUser) {
        if (eventState.equals(EventState.PUBLISHED)) {
            throw new ConflictException("Can't change event not cancelled or in moderation");
        }
    }

    @Override
    public Collection<ParticipationRequestDto> findAllRequestsByEventId(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not found with id = " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event is not found with id = " + eventId));
        Collection<ParticipationRequestDto> result = new ArrayList<>();
        result = participationRequestRepository.findAllByEventId(event).stream()
                .map(RequestMapper::toDto).toList();
        return result;
    }
}
