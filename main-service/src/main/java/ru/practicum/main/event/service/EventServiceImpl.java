package ru.practicum.main.event.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.main.event.model.QEvent;
import ru.practicum.main.event.model.StateAction;
import ru.practicum.main.event.model.StateActionUser;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.location.dto.LocationDto;
import ru.practicum.main.location.dto.LocationNewDto;
import ru.practicum.main.location.dto.LocationUpdateDto;
import ru.practicum.main.location.mapper.LocationMapper;
import ru.practicum.main.location.service.LocationService;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.enums.RequestStatus;
import ru.practicum.main.request.service.RequestService;
import ru.practicum.main.system.exception.AccessDeniedException;
import ru.practicum.main.system.exception.BadConditionsException;
import ru.practicum.main.system.exception.ConstraintViolationException;
import ru.practicum.main.system.exception.NotFoundException;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.service.UserService;
import ru.practicum.main.views.dto.ViewStatDto;
import ru.practicum.main.views.service.ViewService;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final RequestService requestService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final ViewService viewService;
    private final JPAQueryFactory queryFactory;
    private static final int MINIMAL_MINUTES_FOR_CHANGES = 1;

    @Override
    public EventDto getPublic(Long eventId) {
        EventDto event = eventRepository.findById(eventId)
                .filter(x -> x.getState().equals(EventState.PUBLISHED.toString()))
                .map(this::addInfo)
                .orElseThrow(() -> new NotFoundException("Событие с таким id не найдено"));
        return event;
    }

    @Override
    public EventDto get(Long eventId) {
        EventDto event = eventRepository.findById(eventId)
                .map(this::addInfo)
                .orElseThrow(() -> new NotFoundException("Событие с таким id не найдено"));
        return event;
    }

    @Override
    public List<EventDto> get(List<Long> eventIds) {
        return addInfo(eventRepository.findByIdIn(eventIds));
    }

    @Override
    public EventDto getPublished(Long eventId) {
        EventDto event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED.toString())
                .map(this::addInfo)
                .orElseThrow(() -> new NotFoundException("Событие с таким id не найдено"));
        return event;
    }

    @Override
    public List<EventDto> getPublished(List<Long> eventIds) {
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
        checkDateIsGoodThrowError(newEventDto.getEventDate());

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
        event.setState(EventState.PENDING.toString());
        event.setCreatedOn(LocalDateTime.now());
        if (event.getParticipantLimit().equals(0L)) {
            setEventPublished(event);
        }

        EventDto res = addInfo(eventRepository.save(event));
        eventRepository.flush();
        return res;
    }

    @Override
    public boolean existsById(Long id) {
        return eventRepository.existsById(id);
    }

    @Override
    public boolean checkEventsExistInCategory(Long categoryId) {
        return eventRepository.existsByCategoryId(categoryId);
    }

    @Override
    public boolean existsByIdAndInitiatorId(Long eventId, Long userId) {
        return eventRepository.existsByIdAndInitiatorId(eventId, userId);
    }

    @Override
    @Transactional
    public void increaseViews(Long eventId, String ip) {
        if (!existsById(eventId)) {
            throw new NotFoundException("Событие не найдено");
        }
        viewService.add(eventId, ip);
        ViewStatDto views = viewService.stat(eventId);
        eventRepository.setViews(eventId, views.getViews());
        eventRepository.flush();
    }

    @Override
    public List<EventShortDto> getByFilter(EventFilter filter) {
        checkFilterDateRangeIsGood(filter.getRangeStart(), filter.getRangeEnd());

        List<Event> events = new ArrayList<>();
        QEvent event = QEvent.event;
        BooleanExpression exp = Expressions.asBoolean(true).isTrue();

        if (!filter.getIsDtoForAdminApi()) {
            exp = event.state.eq(EventState.PUBLISHED.toString());
        }

        if (filter.getText() != null && !filter.getText().isBlank()) {
            exp = exp.and(event.description.containsIgnoreCase(filter.getText()))
                    .or(event.annotation.containsIgnoreCase(filter.getText()));
        }

        if (filter.getCategories() != null) {
            exp = exp.and(event.categoryId.in(filter.getCategories()));
        }

        if (filter.getPaid() != null) {
            exp = exp.and(event.paid.eq(filter.getPaid()));
        }

        if (filter.getRangeStart() != null) {
            exp = exp.and(event.eventDate.after(filter.getRangeStart()));
        }

        if (filter.getRangeEnd() != null) {
            exp = exp.and(event.eventDate.before(filter.getRangeEnd()));
        }

        if (filter.getOnlyAvailable() != null) {
            exp = exp.and(event.participantLimit.gt(event.confirmedRequests));
        }

        if (filter.getIsDtoForAdminApi()) {
            if (filter.getStates() != null) {
                exp = exp.and(event.state.in(filter.getStates()));
            }

            if (filter.getUsers() != null) {
                exp = exp.and(event.initiatorId.in(filter.getUsers()));
            }
        }

        JPAQuery<Event> query = queryFactory.selectFrom(event)
                .where(exp)
                .offset(filter.getFrom())
                .limit(filter.getSize());

        if ("EVENT_DATE".equals(filter.getSort())) {
            events = query.orderBy(event.eventDate.asc()).fetch();
        } else {
            events = query.orderBy(event.views.desc()).fetch();
        }

        return EventMapper.toShortDtoFromDto(addInfo(events));
    }


    @Override
    @Transactional
    public EventDto updateByAdmin(Long eventId, UpdateEventDto updated) {
        return update(eventId, null, updated);
    }

    @Override
    @Transactional
    public EventDto updateByUser(Long eventId, Long userId, UpdateEventDto updated) {
        return update(eventId, userId, updated);
    }

    @Transactional
    public EventDto update(Long eventId, Long userId, UpdateEventDto updated) {
        boolean isAdminEditThis = userId == null;
        String action = updated.getStateAction();

        checkDateIsGoodThrowError(updated.getEventDate());

        Event event;
        if (isAdminEditThis) {
            event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException("Собатиые не найдено"));
        } else {
            event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                    .orElseThrow(() -> new NotFoundException("Собатиые не найдено"));

            if (event.getState().equals(EventState.PUBLISHED.toString())) {
                throw new BadConditionsException("Нельзя изменить уже опубликованное событие");
            }
        }

        checkDateIsGoodThrowError(event.getEventDate());

        if (updated.getCategory() != null) {
            if (!categoryService.existsById(updated.getCategory())) {
                throw new NotFoundException("Категория не найдена");
            }
            event.setCategoryId(updated.getCategory());
        }

        if (updated.getLocation() != null) {
            updateEventLocationOrCreateNew(event, updated.getLocation());
        }

        if (updated.getAnnotation() != null) {
            event.setAnnotation(updated.getAnnotation());
        }

        if (updated.getPaid() != null) {
            event.setPaid(updated.getPaid());
        }

        if (updated.getEventDate() != null) {
            event.setEventDate(updated.getEventDate());
        }

        if (updated.getDescription() != null) {
            event.setDescription(updated.getDescription());
        }

        if (updated.getTitle() != null) {
            event.setTitle(updated.getTitle());
        }

        if (updated.getParticipantLimit() != null) {
            event.setParticipantLimit(updated.getParticipantLimit());
        }

        if (action != null) {
            if (isAdminEditThis) {
                if (action.equals(StateAction.PUBLISH_EVENT.toString())
                        && event.getState().equals(EventState.PUBLISHED.toString())
                        && !event.getParticipantLimit().equals(0L)) {
                    throw new BadConditionsException("Нельзя опубликовать уже опубликованное событие");
                }
                if (action.equals(StateAction.PUBLISH_EVENT.toString())
                        && event.getState().equals(EventState.CANCELED.toString())) {
                    throw new BadConditionsException("Нельзя опубликовать уже отменённое событие");
                }

                if (action.equals(StateAction.PUBLISH_EVENT.toString())) {
                    setEventPublished(event);
                } else if (action.equals(StateAction.REJECT_EVENT.toString())) {
                    if (event.getState().equals(EventState.PUBLISHED.toString())) {
                        throw new BadConditionsException("Нельзя отменить уже опубликованное событие");
                    }
                    event.setState(EventState.CANCELED.toString());
                }
            } else {
                if (action.equals(StateActionUser.SEND_TO_REVIEW.toString())) {
                    event.setState(EventState.PENDING.toString());
                } else if (action.equals(StateActionUser.CANCEL_REVIEW.toString())) {
                    event.setState(EventState.CANCELED.toString());
                }
            }
        }

        Event res = eventRepository.save(event);
        eventRepository.flush();
        return addInfo(res);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsStatus(Long eventId, Long userId,
            EventRequestStatusUpdateRequest updateRequest) {

        final String REJECTED = RequestStatus.REJECTED.toString();
        final String CONFIRMED = RequestStatus.CONFIRMED.toString();
        String setStatus = RequestStatus.valueOf(updateRequest.getStatus()).toString();

        UserDto user = userService.get(userId);
        EventDto event = get(eventId);

        if (setStatus.equals(CONFIRMED) && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new BadConditionsException(
                    "Нельзя принять ещё одного участника, достигнут лимит");
        }

        if (!event.getInitiatorId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "Пользователь может изменять статус только событиям, которые он сделал сам");
        }

        // если для события лимит заявок равен 0 или отключена пре-модерация заявок, то
        // подтверждение заявок не требуется
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0L)) {
            return result;
        }

        List<Long> requestsIds = updateRequest.getRequestIds();
        List<ParticipationRequestDto> requests = requestService
                .findByEventIdAndIdIn(eventId, requestsIds);

        if (requests.stream().anyMatch(x -> x.getStatus().equals(REJECTED))) {
            throw new BadConditionsException(
                    "Статус можно изменить только у заявок, находящихся в состоянии ожидания");
        }

        if (requests.size() > 0 && setStatus.equals(REJECTED)) {
            requestService.setStatusAll(requestsIds, REJECTED);
            requests.forEach(x -> x.setStatus(REJECTED));
            result.setRejectedRequests(requests);
            return result;
        }

        List<ParticipationRequestDto> requestsConfirmed = requests.stream()
                .filter(x -> x.getStatus().equals(RequestStatus.CONFIRMED.toString()))
                .collect(Collectors.toList());

        List<ParticipationRequestDto> requestsPending = requests.stream()
                .filter(x -> x.getStatus().equals(RequestStatus.PENDING.toString()))
                .toList();

        int availableSlots = (int) (event.getParticipantLimit() - requestsConfirmed.size());
        if (availableSlots < 1L) {
            throw new BadConditionsException(
                    "Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие");
        }
        if (availableSlots > requestsPending.size()) {
            availableSlots = requestsPending.size();
        }

        List<ParticipationRequestDto> toConfirm = requestsPending.subList(
                0, availableSlots);

        if (toConfirm.size() > 0) {
            List<Long> toConfirmIds = toConfirm.stream()
                    .map(ParticipationRequestDto::getId).toList();

            toConfirm.forEach(x -> x.setStatus(RequestStatus.CONFIRMED.toString()));
            requestService.setStatusAll(toConfirmIds, RequestStatus.CONFIRMED.toString());

            List<ParticipationRequestDto> toReject = List.of();
            if (availableSlots < requestsPending.size()) {
                toReject = requestsPending.subList(
                        availableSlots, requestsPending.size());

                List<Long> toRejectIds = toConfirm.stream()
                        .map(ParticipationRequestDto::getId).toList();

                requestService.setStatusAll(toRejectIds, REJECTED);
            }

            requestsConfirmed.addAll(toConfirm);
            setConfirmedRequestsCount(event.getInitiatorId(), (long) requestsConfirmed.size());
            result.setConfirmedRequests(requestsConfirmed);
            result.setRejectedRequests(toReject);
        }

        eventRepository.flush();
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

    @Transactional
    public void setConfirmedRequestsCount(Long eventId, Long requestsCount) {
        eventRepository.setContirmedRequestsCount(eventId, requestsCount);
        eventRepository.flush();
    }

    private List<EventDto> addInfo(List<Event> events) {
        List<Long> eventsIds = events.stream().map(Event::getId).toList();
        List<Long> categoryIds = events.stream().map(Event::getCategoryId).toList();
        List<Long> usersIds = events.stream().map(Event::getInitiatorId).toList();
        List<Long> locationsIds = events.stream().map(Event::getLocationId).toList();

        List<CategoryDto> categories = categoryService.get(categoryIds);
        List<UserDto> users = userService.get(usersIds);
        List<LocationDto> locations = locationService.get(locationsIds);
        Map<Long, Long> requests = requestService.getConfirmedEventsRequestsCount(eventsIds);

        Map<Long, CategoryDto> catsByIds = categories.stream()
                .collect(Collectors.toMap(CategoryDto::getId, Function.identity()));

        Map<Long, UserDto> usersByIds = users.stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));

        Map<Long, LocationDto> locationsByIds = locations.stream()
                .collect(Collectors.toMap(LocationDto::getId, Function.identity()));

        return events.stream()
                .map(e -> {
                    EventDto x = EventMapper.toDto(e);
                    x.setConfirmedRequests(requests.getOrDefault(x.getId(), 0L));
                    x.setCategory(catsByIds.getOrDefault(x.getCategoryId(), null));
                    x.setInitiator(usersByIds.getOrDefault(x.getInitiatorId(), null));
                    x.setLocation(locationsByIds.getOrDefault(e.getLocationId(), null));
                    return x;
                })
                .toList();
    }

    public EventDto addInfo(Event event) {
        return addInfo(List.of(event)).get(0);
    }

    @Transactional
    public void updateEventLocationOrCreateNew(Event event, LocationUpdateDto location) {
        Float lon = location.getLon();
        Float lat = location.getLat();

        if (locationService.existsByLonAndLat(lon, lat)) {
            LocationDto locationSaved = locationService.getByLonAndLat(lon, lat);
            event.setLocationId(locationSaved.getId());
        } else {
            LocationNewDto locationNew = LocationMapper.fromUpdateToNew(location);
            event.setLocationId(locationService.create(locationNew).getId());
        }
    }

    private void checkDateIsGoodThrowError(LocalDateTime date) {
        if (date == null) {
            return;
        }

        if (!date.isAfter(LocalDateTime.now().plusMinutes(MINIMAL_MINUTES_FOR_CHANGES))) {
            throw new ConstraintViolationException(
                    "Событие можно запланировать или изменить минимум за " + MINIMAL_MINUTES_FOR_CHANGES
                            + " минут до его начала");
        }
    }

    private void checkFilterDateRangeIsGood(LocalDateTime dateBegin, LocalDateTime dateEnd) {
        if (dateBegin == null) {
            return;
        }
        if (dateEnd == null) {
            return;
        }

        if (dateBegin.isAfter(dateEnd)) {
            throw new ConstraintViolationException(
                    "Неверно задана дата начала и конца события в фильтре");
        }
    }

    private void setEventPublished(Event event) {
        event.setState(EventState.PUBLISHED.toString());
        event.setPublishedOn(LocalDateTime.now());
    }
}
