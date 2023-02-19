package ru.practicum.explore_with_me.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.category.Category;
import ru.practicum.explore_with_me.category.CategoryRepository;
import ru.practicum.explore_with_me.event.comment.Comment;
import ru.practicum.explore_with_me.event.comment.CommentMapper;
import ru.practicum.explore_with_me.event.comment.CommentRepository;
import ru.practicum.explore_with_me.event.comment.CommentStatus;
import ru.practicum.explore_with_me.event.comment.dto.CommentDto;
import ru.practicum.explore_with_me.event.comment.dto.NewCommentDto;
import ru.practicum.explore_with_me.event.comment.dto.RejectionCommentRequest;
import ru.practicum.explore_with_me.event.comment.dto.UpdateCommentRequest;
import ru.practicum.explore_with_me.event.dto.*;
import ru.practicum.explore_with_me.exception.ErrorStatus;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.exception.ValidationException;
import ru.practicum.explore_with_me.request.Request;
import ru.practicum.explore_with_me.request.RequestMapper;
import ru.practicum.explore_with_me.request.RequestRepository;
import ru.practicum.explore_with_me.request.RequestStates;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.user.User;
import ru.practicum.explore_with_me.user.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository repository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final RequestRepository requestRepository;

    private final CommentRepository commentRepository;

    // Создание нового события
    @Override
    public EventFullDto saveNewEvent(Long userId, NewEventDto eventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(null, ErrorStatus.NOT_FOUND, "User was not found.",
                        String.format("User with id=%s was not found.", userId), LocalDateTime.now()));
        Category category = checkCategory(eventDto.getCategory());
        Event event = EventMapper.makeEvent(eventDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventStates.PENDING);
        EventFullDto eventFullDto = EventMapper.makeEventFullDto(repository.save(event));
        log.info("Event with id={} was saved", eventFullDto.getId());
        return eventFullDto;
    }

    // Обновление события инициатором
    @Override
    public EventFullDto updateEventByInitiator(Long userId, UpdateEventRequest updateEvent) {
        Event event = getEvent(updateEvent.getEventId());
        checkInitiator(userId, event);
        if ((event.getState() == EventStates.CANCELED) || (event.getState() == EventStates.PENDING)) {
            updateEvent(event, updateEvent);
            event.setState(EventStates.PENDING);
            log.info("Event with id={} was updated", event.getId());
            return EventMapper.makeEventFullDto(repository.save(event));
        } else {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "You can't change published event.",
                    String.format("Event with id=%s is already published. You can't change published event.",
                            event.getId()),
                    LocalDateTime.now());
        }
    }

    // Обновление события администратором
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest updateEvent) {
        Event event = getEvent(eventId);
        updateEvent(event, updateEvent);
        log.info("Event with id={} was updated", event.getId());
        return EventMapper.makeEventFullDto(repository.save(event));
    }

    // Получение события инициатором
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByInitiator(Long userId, Integer from, Integer size) {
        return repository.findAllByInitiator_Id(userId, PageRequest.of(from / size, size, Sort.by("id")))
                .stream()
                .map(EventMapper::makeEventShortDto)
                .collect(Collectors.toList());
    }

    // Поиск события администратором
    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsFromAdminController(EventAdminSearch eventSearch) {
        eventSearch.setStates(Optional.ofNullable(eventSearch.getStates()).orElse(Set.of(EventStates.values())));
        eventSearch.setRangeStart(Optional.ofNullable(eventSearch.getRangeStart())
                .orElse(Timestamp.valueOf(LocalDateTime.now())));
        eventSearch.setRangeEnd(Optional.ofNullable(eventSearch.getRangeEnd())
                .orElse(Timestamp.valueOf(LocalDateTime.now().plusYears(100))));
        return setCommentsForEvents(repository.getEventsByAdmin(eventSearch.getUsers(), eventSearch.getStates(),
                        eventSearch.getCategories(), eventSearch.getRangeStart(), eventSearch.getRangeEnd(),
                        eventSearch.getPageable()).stream()
                .map(EventMapper::makeEventFullDto)
                .collect(Collectors.toList()));
    }

    // Поиск событиЙ с параметрами из публичного контроллера
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsFromPublicController(EventPublicSearch eventSearch) {
        eventSearch.setRangeStart(Optional.ofNullable(eventSearch.getRangeStart())
                .orElse(Timestamp.valueOf(LocalDateTime.now())));
        eventSearch.setRangeEnd(Optional.ofNullable(eventSearch.getRangeEnd())
                .orElse(Timestamp.valueOf(LocalDateTime.now().plusYears(100))));
        if (eventSearch.isOnlyAvailable()) {
            return getOnlyAvailableEvents(eventSearch);
        }
        return getEvents(eventSearch);
    }

    // Публикация события
    @Override
    public EventFullDto publishEvent(Long eventId) {
        Event event = getEvent(eventId);
        if (event.getState().equals(EventStates.PUBLISHED)) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "The event is already published.",
                    String.format("Event with id=%s already published.", eventId),
                    LocalDateTime.now());
        } else if (event.getState().equals(EventStates.CANCELED)) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "The event was canceled.",
                    String.format("Event with id=%s was canceled.", eventId),
                    LocalDateTime.now());
        } else if (event.getEventDate().before(Timestamp.valueOf(LocalDateTime.now()))) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "Date of the event in the past.",
                    String.format("Date of the event with id=%s in the past.", eventId),
                    LocalDateTime.now());
        }
        event.setState(EventStates.PUBLISHED);
        event.setPublishedOn(Timestamp.from(Instant.now()));
        log.info("Event with id={} was published", eventId);
        return EventMapper.makeEventFullDto(repository.save(event));
    }

    // Отмена события
    @Override
    public EventFullDto rejectEvent(Long eventId) {
        Event event = getEvent(eventId);
        if (event.getState().equals(EventStates.PUBLISHED)) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "The event is already published.",
                    String.format("Event with id=%s already published.", eventId),
                    LocalDateTime.now());
        } else if (event.getState().equals(EventStates.CANCELED)) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "The event is already canceled.",
                    String.format("Event with id=%s is already canceled.", eventId),
                    LocalDateTime.now());
        }
        event.setState(EventStates.CANCELED);
        log.info("Event with id={} was canceled", eventId);
        return EventMapper.makeEventFullDto(repository.save(event));
    }

    // Отмена события инициатором
    @Override
    public EventFullDto rejectedEventByInitiator(Long userId, Long eventId) {
        Event event = getEvent(eventId);
        checkInitiator(userId, event);
        if (event.getState() == EventStates.PENDING) {
            event.setState(EventStates.CANCELED);
            log.info("Event with id={} was canceled by initiator.", eventId);
            return EventMapper.makeEventFullDto(repository.save(event));
        }
        throw new ValidationException(null, ErrorStatus.CONFLICT, "You can rejected only pending state event.",
                String.format("Event with id=%s has not pending state.", event.getId()),
                LocalDateTime.now());
    }

    // Получение события по id для публичного контроллера
    @Override
    public EventFullDto getEventsByIdFromPublicController(Long id) {
        Event event = getEvent(id);
        if (event.getState() == EventStates.PUBLISHED) {
            event.setViews(event.getViews() + 1);
            repository.save(event);
            List<Comment> comments = commentRepository.findAllByEventId(id);
            event.setComments(comments);
            return EventMapper.makeEventFullDto(event);
        }
        return null;
    }

    // Получение события по id для приватного контроллера
    @Override
    public EventFullDto getEventsByIdFromPrivateController(Long userId, Long eventId) {
        Event event = getEvent(eventId);
        if ((event.getState() == EventStates.PUBLISHED) || (event.getInitiator().getId().equals(userId))) {
            List<Comment> comments = commentRepository.findAllByEventId(eventId);
            event.setComments(comments);
            return EventMapper.makeEventFullDto(event);
        }
        return null;
    }

    // Подтверждение инициатором события запроса на участие
    @Override
    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) {
        Event event = getEvent(eventId);
        Request request = getRequest(reqId);
        checkInitiator(userId, event);
        if (!Objects.equals(request.getEvent().getId(), eventId)) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "Request refers to another event",
                    String.format("Request with id=%s doesn't refer for event with id=%s.", reqId, eventId),
                    LocalDateTime.now());
        }
        if (!Objects.equals(request.getStatus(), RequestStates.PENDING)) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "You can confirm only pending request.",
                    String.format("Request with id=%s has %s status.", reqId, request.getStatus()),
                    LocalDateTime.now());
        }
        if ((event.getParticipantLimit() == 0) || !event.isRequestModeration()) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "Request moderation is not required.",
                    String.format("For event with id=%s request moderation is not required.", event.getId()),
                    LocalDateTime.now());
        } else if (event.getParticipantLimit() == setConfirmedRequests(event)) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "Requests limit has been reached.",
                    String.format("For event with id=%s request limit has been reached.", event.getId()),
                    LocalDateTime.now());
        }

        request.setStatus(RequestStates.CONFIRMED);
        checkOtherRequests(event);
        repository.save(event);
        return RequestMapper.makeRequestDto(requestRepository.save(request));
    }

    // Отмена запроса на участие
    @Override
    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId) {
        Event event = getEvent(eventId);
        checkInitiator(userId, event);
        Request request = getRequest(reqId);
        request.setStatus(RequestStates.REJECTED);
        return RequestMapper.makeRequestDto(requestRepository.save(request));
    }

    // Получение инициатором всех запросов на участие в событии
    @Override
    public List<ParticipationRequestDto> getRequestsByEventIdByInitiator(Long userId, Long eventId) {
        Event event = getEvent(eventId);
        checkInitiator(userId, event);
        return requestRepository.findAllByEvent_Id(eventId).stream().map(RequestMapper::makeRequestDto)
                .collect(Collectors.toList());
    }

    // Поиск по параметрам только среди доступных событий
    private List<EventShortDto> getOnlyAvailableEvents(EventPublicSearch eventSearch) {
        if (Objects.equals(eventSearch.getSort().name().toUpperCase(), SortVariants.VIEWS.name().toUpperCase())) {
            return repository.getOnlyAvailableEventsOrderByViews(eventSearch.getText(), eventSearch.getCategories(),
                            eventSearch.getPaid(), eventSearch.getRangeStart(), eventSearch.getRangeEnd(),
                            Timestamp.valueOf(LocalDateTime.now()), eventSearch.getPageable()).stream()
                    .map(EventMapper::makeEventShortDto).collect(Collectors.toList());
        }
        return repository.getOnlyAvailableEventsOrderByEventDate(eventSearch.getText(), eventSearch.getCategories(),
                        eventSearch.getPaid(), eventSearch.getRangeStart(), eventSearch.getRangeEnd(),
                        Timestamp.valueOf(LocalDateTime.now()), eventSearch.getPageable()).stream()
                .map(EventMapper::makeEventShortDto).collect(Collectors.toList());
    }

    // Поиск событий по параметрам
    private List<EventShortDto> getEvents(EventPublicSearch eventSearch) {
        if (Objects.equals(eventSearch.getSort().name().toUpperCase(), SortVariants.VIEWS.name().toUpperCase())) {
            return repository.getEventsOrderByViews(eventSearch.getText(), eventSearch.getCategories(),
                            eventSearch.getPaid(), eventSearch.getRangeStart(), eventSearch.getRangeEnd(),
                            Timestamp.valueOf(LocalDateTime.now()), eventSearch.getPageable()).stream()
                    .map(EventMapper::makeEventShortDto).collect(Collectors.toList());
        }
        return repository.getEventsOrderByEventDate(eventSearch.getText(), eventSearch.getCategories(),
                        eventSearch.getPaid(), eventSearch.getRangeStart(), eventSearch.getRangeEnd(),
                        Timestamp.valueOf(LocalDateTime.now()), eventSearch.getPageable()).stream()
                .map(EventMapper::makeEventShortDto).collect(Collectors.toList());
    }

    // Проверка инициатора события
    private void checkInitiator(Long userId, Event event) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "The user is not event initiator.",
                    String.format("User with id=%s is not event initiator.", userId),
                    LocalDateTime.now());
        }
    }

    // Проверка категории
    private Category checkCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(null, ErrorStatus.NOT_FOUND, "Category was not found.",
                        String.format("Category with id=%s was not found.", categoryId), LocalDateTime.now()));
    }

    // Получение события
    private Event getEvent(Long eventId) {
        return repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(null, ErrorStatus.NOT_FOUND, "Event was not found.",
                        String.format("Event with id=%s was not found.", eventId), LocalDateTime.now()));
    }

    // Получение запроса на участие
    private Request getRequest(Long reqId) {
        return requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException(null, ErrorStatus.NOT_FOUND, "Request was not found.",
                        String.format("Request with id=%s was not found.", reqId), LocalDateTime.now()));
    }

    // Обновление полей события
    private void updateEvent(Event event, UpdateEventRequest updateEvent) {
        event.setCategory(checkCategory(updateEvent.getCategory()));
        Optional.ofNullable(updateEvent.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEvent.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEvent.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEvent.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEvent.getTitle()).ifPresent(event::setTitle);
    }

    // Сохранение комментария
    @Override
    public CommentDto saveNewComment(Long userId, Long eventId, NewCommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(null, ErrorStatus.NOT_FOUND,
                "User was not found.", String.format("User with id=%s was not found.", userId),
                LocalDateTime.now()));
        Comment comment = CommentMapper.makeComment(commentDto);
        comment.setEvent(getEvent(eventId));
        comment.setUser(user);
        comment.setAuthorName(user.getName());
        comment.setCreated(Timestamp.from(Instant.now()));
        comment.setStatus(CommentStatus.PUBLISHED);
        return CommentMapper.makeCommentDto(commentRepository.save(comment));
    }

    // Отмена комментария администратором
    @Override
    public void rejectComment(Long eventId, RejectionCommentRequest commentRequest) {
        Comment comment = getComment(commentRequest.getId());
        comment.setStatus(CommentStatus.REJECTED);
        comment.setRejectionReason(commentRequest.getRejectionReason());
        commentRepository.save(comment);
        log.info("Comment with id={} was rejected", comment.getId());
    }

    // Обновление комментария
    @Override
    public CommentDto updateComment(Long userId, Long eventId, UpdateCommentRequest updateCommentRequest) {
        Comment comment = getComment(updateCommentRequest.getId());
        if (!comment.getUser().getId().equals(userId)) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "You can't change other users comments.",
                    String.format("Comment with id=%s was left by another user.",
                            comment.getId()), LocalDateTime.now());
        }
        if (!comment.getEvent().getId().equals(eventId)) {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "Wrong event id.",
                    String.format("Comment with id=%s was left for another event.",
                            comment.getId()), LocalDateTime.now());
        }
        if (comment.getStatus().equals(CommentStatus.REJECTED)) {
            comment.setStatus(CommentStatus.PUBLISHED);
        }
        comment.setText(updateCommentRequest.getText());
        comment.setUpdated(Timestamp.from(Instant.now()));
        log.info("Comment with id={} has been updated", comment.getId());
        return CommentMapper.makeCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> searchCommentByAuthor(Long userId, Timestamp rangeStart, Timestamp rangeEnd,
                                                  List<CommentStatus> statuses, Integer from, Integer size) {
        statuses = Optional.ofNullable(statuses).orElse(List.of(CommentStatus.values()));
        rangeStart = Optional.ofNullable(rangeStart).orElse(Timestamp.valueOf(LocalDateTime.now()));
        rangeEnd = Optional.ofNullable(rangeEnd).orElse(Timestamp.valueOf(LocalDateTime.now().plusYears(100)));

        return commentRepository.findCommentsByAuthor(userId, statuses, rangeStart, rangeEnd,
                        PageRequest.of(from / size, size)).stream().map(CommentMapper::makeCommentDto)
                .collect(Collectors.toList());



//        if ((rangeStart == null) || (rangeEnd == null)) {
//            return commentRepository.searchCommentByAuthor(userId, rangeStart, rangeEnd, statuses,
//                    PageRequest.of(from / size, size)).stream().map(CommentMapper::makeCommentDto)
//                    .collect(Collectors.toList());
//        }
//        return commentRepository.searchCommentByAuthorWithoutTime(userId, statuses,
//                        PageRequest.of(from / size, size)).stream().map(CommentMapper::makeCommentDto)
//                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getComments(List<Long> users, List<Long> events, List<CommentStatus> statuses,
                                        Timestamp rangeStart, Timestamp rangeEnd, Integer from, Integer size) {
        if (statuses == null) {
            statuses = new ArrayList<>();
            statuses.add(CommentStatus.REJECTED);
            statuses.add(CommentStatus.PUBLISHED);
        }
        if (rangeStart == null) {
            rangeStart = Timestamp.from(Instant.ofEpochMilli(0));
        }
        if (rangeEnd == null) {
            rangeEnd = Timestamp.from(Instant.now());
        }
        if (users == null) {
            return commentRepository.searchCommentByAdminWithoutUsers(events, rangeStart, rangeEnd, statuses,
                            PageRequest.of(from / size, size)).stream().map(CommentMapper::makeCommentDto)
                    .collect(Collectors.toList());
        } else if (events == null) {
            return commentRepository.searchCommentByAdminWithoutEvents(users, rangeStart, rangeEnd, statuses,
                            PageRequest.of(from / size, size)).stream().map(CommentMapper::makeCommentDto)
                    .collect(Collectors.toList());
        }
        return commentRepository.searchCommentByAdmin(users, events, rangeStart, rangeEnd, statuses,
                        PageRequest.of(from / size, size)).stream().map(CommentMapper::makeCommentDto)
                .collect(Collectors.toList());
    }

    private void checkOtherRequests(Event event) {
        if (event.getParticipantLimit() == setConfirmedRequests(event)) {
            List<Request> requests = requestRepository.findAllByEvent_Id(event.getId());
            for (Request r : requests) {
                r.setStatus(RequestStates.REJECTED);
            }
            requestRepository.saveAll(requests);
        }
    }

    private Comment getComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException(null, ErrorStatus.NOT_FOUND, "The comment object was not found.",
                        String.format("Comment with id=%s was not found.", commentId),
                        LocalDateTime.now()));
    }

    private List<EventFullDto> setCommentsForEvents(List<EventFullDto> eventFullDtos) {
        List<Long> eventsIds = eventFullDtos.stream().map(EventFullDto::getId).collect(Collectors.toList());
        List<CommentDto> commentDtos = commentRepository.findAllByIds(eventsIds).stream()
                .map(CommentMapper::makeCommentDto).collect(Collectors.toList());
        for (EventFullDto e : eventFullDtos) {
            e.setComments(commentDtos.stream().filter(c -> c.getEventId().equals(e.getId())).collect(Collectors.toList()));
        }
        return eventFullDtos;
    }

    private int setConfirmedRequests(Event event) {
        return (int) event.getRequests()
                .stream()
                .filter(r -> r.getStatus().equals(RequestStates.CONFIRMED))
                .map(Request::getId).count();
    }
}
