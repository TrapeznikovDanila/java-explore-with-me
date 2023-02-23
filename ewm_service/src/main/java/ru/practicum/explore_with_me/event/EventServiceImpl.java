package ru.practicum.explore_with_me.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.category.Category;
import ru.practicum.explore_with_me.category.CategoryRepository;
import ru.practicum.explore_with_me.event.dto.*;
import ru.practicum.explore_with_me.exception.ErrorStatus;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.exception.ValidationException;
import ru.practicum.explore_with_me.request.Request;
import ru.practicum.explore_with_me.request.RequestMapper;
import ru.practicum.explore_with_me.request.RequestRepository;
import ru.practicum.explore_with_me.request.RequestStates;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.user.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository repository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final RequestRepository requestRepository;

    @Override
    public EventFullDto saveNewEvent(long userId, NewEventDto eventDto) {
        Boolean paid = eventDto.isPaid();
        if (paid == null) {
            eventDto.setPaid(false);
        }
        Boolean requestModeration = eventDto.isRequestModeration();
        if (requestModeration == null) {
            eventDto.setRequestModeration(true);
        }
        if (eventDto.getParticipantLimit() == null) {
            eventDto.setParticipantLimit(0);
        }
        Event event = EventMapper.makeEvent(eventDto);
        event.setCategory(categoryRepository.findById(eventDto.getCategory()).get());
        event.setInitiator(userRepository.findById(userId).get());
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventStates.PENDING);
        return EventMapper.makeEventFullDto(repository.save(event));
    }

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<EventStates> states, List<Long> categories, Timestamp rangeStart,
                                        Timestamp rangeEnd, Integer from, Integer size) {
        if (states == null) {
            states = new ArrayList<>();
            states.add(EventStates.PENDING);
            states.add(EventStates.CANCELED);
            states.add(EventStates.PUBLISHED);
        }
        if ((users == null) && (categories != null) && ((rangeStart != null) || (rangeEnd != null))) {
            return getEventsWithoutUsers(states, categories, rangeStart, rangeEnd, from, size);
        } else if ((users != null) && (categories == null) && ((rangeStart != null) || (rangeEnd != null))) {
            return getEventsWithoutCategories(users, states, rangeStart, rangeEnd, from, size);
        } else if ((users != null) && (categories != null) && ((rangeStart == null) || (rangeEnd == null))) {
            return getEventsWithoutTime(users, states, categories, from, size);
        } else if (users != null && categories == null) {
            return getEventsOnlyWithUsersAndStates(users, states, from, size);
        } else if (users == null && categories != null) {
            return getEventsOnlyWithCategoriesAndStates(states, categories, from, size);
        } else if (users == null && rangeStart != null && rangeEnd != null) {
            return getEventsOnlyWithTimeAndStates(states, rangeStart, rangeEnd, from, size);
        }
        return repository.findAllWithAllParameters(users, states, categories, rangeStart, rangeEnd,
                        PageRequest.of(from / size, size))
                .stream()
                .map(EventMapper::makeEventFullDto)
                .collect(Collectors.toList());
    }

    private List<EventFullDto> getEventsWithoutUsers(List<EventStates> states, List<Long> categories, Timestamp rangeStart,
                                                     Timestamp rangeEnd, Integer from, Integer size) {
        return repository.findAllWithoutUsers(states, categories, rangeStart, rangeEnd,
                        PageRequest.of(from / size, size))
                .stream()
                .map(EventMapper::makeEventFullDto)
                .collect(Collectors.toList());
    }

    private List<EventFullDto> getEventsWithoutCategories(List<Long> users, List<EventStates> states, Timestamp rangeStart,
                                                          Timestamp rangeEnd, Integer from, Integer size) {
        return repository.findAllWithoutCategories(users, states, rangeStart, rangeEnd,
                        PageRequest.of(from / size, size))
                .stream()
                .map(EventMapper::makeEventFullDto)
                .collect(Collectors.toList());
    }

    private List<EventFullDto> getEventsWithoutTime(List<Long> users, List<EventStates> states, List<Long> categories,
                                                    Integer from, Integer size) {
        return repository.findAllWithoutTime(users, states, categories, PageRequest.of(from / size, size))
                .stream()
                .map(EventMapper::makeEventFullDto)
                .collect(Collectors.toList());
    }

    private List<EventFullDto> getEventsOnlyWithUsersAndStates(List<Long> users, List<EventStates> states, Integer from,
                                                               Integer size) {
        return repository.findAllOnlyWithUsersAndStates(users, states, PageRequest.of(from / size, size))
                .stream()
                .map(EventMapper::makeEventFullDto)
                .collect(Collectors.toList());
    }

    private List<EventFullDto> getEventsOnlyWithCategoriesAndStates(List<EventStates> states, List<Long> categories,
                                                                    Integer from, Integer size) {
        return repository.findAllOnlyWithCategoriesAndStates(states, categories, PageRequest.of(from / size, size))
                .stream()
                .map(EventMapper::makeEventFullDto)
                .collect(Collectors.toList());
    }

    private List<EventFullDto> getEventsOnlyWithTimeAndStates(List<EventStates> states, Timestamp rangeStart,
                                                              Timestamp rangeEnd, Integer from, Integer size) {
        return repository.findAllOnlyWithTimeAndStates(states, rangeStart, rangeEnd,
                        PageRequest.of(from / size, size))
                .stream()
                .map(EventMapper::makeEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsByInitiator(long userId, Integer from, Integer size) {
        return repository.findAllByInitiator_Id(userId, PageRequest.of(from / size, size))
                .stream()
                .map(EventMapper::makeEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsFromPublicController(String text, List<Long> categories, Boolean paid, Timestamp rangeStart,
                                                             Timestamp rangeEnd, Boolean onlyAvailable, SortVariants sort, Integer from,
                                                             Integer size) {
        if ((text != null) && (categories != null) && (paid != null) && (rangeStart != null) && (rangeEnd != null)
                && (sort != null)) {
            String pattern = "%" + text + "%";
            if (sort.equals(SortVariants.EVENT_DATE)) {
                return getEventSortedByEventDate(pattern, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size);
            } else {
                return getEventSortedByViews(pattern, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size);
            }
        } else {
            return repository.findAll().stream().map(EventMapper::makeEventShortDto).collect(Collectors.toList());
        }

    }

    @Override
    public EventFullDto publishEvent(long eventId) {
        Event event = getEvent(eventId);
        event.setState(EventStates.PUBLISHED);
        event.setPublishedOn(Timestamp.from(Instant.now()));
        return EventMapper.makeEventFullDto(repository.save(event));
    }

    @Override
    public EventFullDto rejectEvent(long eventId) {
        Event event = getEvent(eventId);
        event.setState(EventStates.CANCELED);
        return EventMapper.makeEventFullDto(repository.save(event));
    }

    @Override
    public EventFullDto getEventsByIdFromPublicController(long id) {
        Event event = getEvent(id);
        if (event.getState() == EventStates.PUBLISHED) {
            event.setViews(event.getViews() + 1);
            repository.save(event);
            return EventMapper.makeEventFullDto(event);
        }
        return null;
    }

    @Override
    public EventFullDto getEventsByIdFromPrivateController(long userId, long eventId) {
        Event event = getEvent(eventId);
        if ((event.getState() == EventStates.PUBLISHED) || (event.getInitiator().getId() == userId)) {
            return EventMapper.makeEventFullDto(event);
        }
        return null;
    }

    @Override
    public EventFullDto updateEventByInitiator(long userId, UpdateEventRequest updateEventRequest) {
        Event event = getEvent(updateEventRequest.getEventId());
        checkInitiator(userId, event);
        if ((event.getState() == EventStates.CANCELED) || (event.getState() == EventStates.PENDING)) {
            updateEventFieldsFromPrivateController(event, updateEventRequest);
            event.setState(EventStates.PENDING);
            return EventMapper.makeEventFullDto(repository.save(event));
        } else {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "You can't change published event.",
                    String.format("Event with id=%s is already published. You can't change published event.",
                            event.getId()),
                    LocalDateTime.now());
        }
    }

    @Override
    public EventFullDto updateEventByAdmin(long eventId, AdminUpdateEventRequest updateEventRequest) {
        Event event = getEvent(eventId);
        updateEventFieldsFromAdminController(event, updateEventRequest);
        return EventMapper.makeEventFullDto(repository.save(event));
    }

    @Override
    public EventFullDto rejectedEventByInitiator(long userId, long eventId) {
        Event event = getEvent(eventId);
        checkInitiator(userId, event);
        if (event.getState() == EventStates.PENDING) {
            event.setState(EventStates.CANCELED);
            return EventMapper.makeEventFullDto(repository.save(event));
        }
        throw new ValidationException(null, ErrorStatus.CONFLICT, "You can rejected only pending state event.",
                String.format("Event with id=%s has not pending state.", event.getId()),
                LocalDateTime.now());
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByInitiator(long userId, long eventId) {
        Event event = getEvent(eventId);
        checkInitiator(userId, event);
        return requestRepository.findAllByEvent_Id(eventId).stream().map(RequestMapper::makeRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto confirmRequest(long userId, long eventId, long reqId) {
        Event event = getEvent(eventId);
        checkInitiator(userId, event);
        if ((event.getParticipantLimit() == 0) || !event.isRequestModeration()) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "Request moderation is not required.",
                    String.format("For event with id=%s request moderation is not required.", event.getId()),
                    LocalDateTime.now());
        } else if (event.getParticipantLimit() == event.getConfirmedRequests()) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "Requests limit has been reached.",
                    String.format("For event with id=%s request limit has been reached.", event.getId()),
                    LocalDateTime.now());
        }
        Request request = getRequest(reqId);
        request.setStatus(RequestStates.CONFIRMED);
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        checkOtherRequests(event);
        repository.save(event);
        return RequestMapper.makeRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto rejectRequest(long userId, long eventId, long reqId) {
        Event event = getEvent(eventId);
        checkInitiator(userId, event);
        Request request = getRequest(reqId);
        request.setStatus(RequestStates.REJECTED);
        return RequestMapper.makeRequestDto(requestRepository.save(request));
    }

    private Event getEvent(long eventId) {
        Optional<Event> eventOptional = repository.findById(eventId);
        if (eventOptional.isPresent()) {
            return eventOptional.get();

        }
        throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The event object was not found.",
                String.format("Event with id=%s was not found.", eventId),
                LocalDateTime.now());
    }

    private Request getRequest(long reqId) {
        Optional<Request> requestOptional = requestRepository.findById(reqId);
        if (requestOptional.isPresent()) {
            return requestOptional.get();

        }
        throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The request object was not found.",
                String.format("Request with id=%s was not found.", reqId),
                LocalDateTime.now());
    }

    private void checkOtherRequests(Event event) {
        if (event.getParticipantLimit() == event.getConfirmedRequests()) {
            List<Request> requests = requestRepository.findAllByEvent_Id(event.getId());
            for (Request r : requests) {
                r.setStatus(RequestStates.REJECTED);
            }
            requestRepository.saveAll(requests);
        }
    }

    private void checkInitiator(long userId, Event event) {
        if (event.getInitiator().getId() != userId) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "The user is not event initiator.",
                    String.format("User with id=%s is not event initiator.", userId),
                    LocalDateTime.now());
        }
    }

    private void updateEventFieldsFromPrivateController(Event event, UpdateEventRequest updateEventRequest) {
        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getCategory() != null) {
            Optional<Category> categoryOpt = categoryRepository.findById(updateEventRequest.getCategory());
            if (categoryOpt.isPresent()) {
                event.setCategory(categoryOpt.get());
            } else {
                throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The category object was not found.",
                        String.format("Category with id=%s was not found.", updateEventRequest.getCategory()),
                        LocalDateTime.now());
            }
        }
        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }
    }

    private void updateEventFieldsFromAdminController(Event event, AdminUpdateEventRequest updateEventRequest) {
        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getCategory() != null) {
            Optional<Category> categoryOpt = categoryRepository.findById(updateEventRequest.getCategory());
            if (categoryOpt.isPresent()) {
                event.setCategory(categoryOpt.get());
            } else {
                throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The category object was not found.",
                        String.format("Category with id=%s was not found.", updateEventRequest.getCategory()),
                        LocalDateTime.now());
            }
        }
        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getEventDate() != null) {
            event.setEventDate(updateEventRequest.getEventDate());
        }
        if (updateEventRequest.getLocation() != null) {
            event.setLocationLat(updateEventRequest.getLocation().getLat());
            event.setLocationLon(updateEventRequest.getLocation().getLon());
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        }
        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }
    }

    private List<EventShortDto> getEventSortedByEventDate(String pattern, List<Long> categories, Boolean paid, Timestamp rangeStart,
                                                          Timestamp rangeEnd, Boolean onlyAvailable, Integer from,
                                                          Integer size) {
        if (onlyAvailable) {
            return repository.findAllOrderByEventDateOnlyAvailable(pattern, categories, paid, rangeStart, rangeEnd,
                            LocalDateTime.now(), PageRequest.of(from / size, size)).stream()
                    .map(EventMapper::makeEventShortDto)
                    .collect(Collectors.toList());
        }
        return repository.findAllOrderByEventDate(pattern, categories, paid, rangeStart, rangeEnd,
                        Timestamp.valueOf(LocalDateTime.now()), PageRequest.of(from / size, size)).stream()
                .map(EventMapper::makeEventShortDto)
                .collect(Collectors.toList());
    }

    private List<EventShortDto> getEventSortedByViews(String pattern, List<Long> categories, Boolean paid, Timestamp rangeStart,
                                                      Timestamp rangeEnd, Boolean onlyAvailable, Integer from,
                                                      Integer size) {
        if (onlyAvailable) {
            return repository.findAllOrderByViewsOnlyAvailable(pattern, categories, paid, rangeStart, rangeEnd,
                            Timestamp.valueOf(LocalDateTime.now()), PageRequest.of(from / size, size)).stream()
                    .map(EventMapper::makeEventShortDto)
                    .collect(Collectors.toList());
        }
        return repository.findAllOrderByViews(pattern, categories, paid, rangeStart, rangeEnd,
                        Timestamp.valueOf(LocalDateTime.now()), PageRequest.of(from / size, size)).stream()
                .map(EventMapper::makeEventShortDto)
                .collect(Collectors.toList());
    }
}
