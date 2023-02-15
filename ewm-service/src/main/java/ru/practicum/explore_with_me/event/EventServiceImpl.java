package ru.practicum.explore_with_me.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        if ((event.getState() == EventStates.CANCELED) || (event.getState() == EventStates.PENDING)) {
            checkInitiator(userId, event);
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
    public List<EventFullDto> getEvents(EventAdminSearch eventSearch) {
        eventSearch.setStates(Optional.ofNullable(eventSearch.getStates()).orElse(Set.of(EventStates.values())));
        eventSearch.setRangeStart(Optional.ofNullable(eventSearch.getRangeStart())
                .orElse(Timestamp.valueOf(LocalDateTime.now())));
        eventSearch.setRangeEnd(Optional.ofNullable(eventSearch.getRangeEnd())
                .orElse(Timestamp.valueOf(LocalDateTime.now().plusYears(100))));
        if ((eventSearch.getUsers() == null) && !(eventSearch.getCategories() == null)) {
            return getEventsWithoutUsers(eventSearch);
        } else if (!(eventSearch.getUsers() == null) && (eventSearch.getCategories() == null)) {
            return getEventsWithoutCategories(eventSearch);
        } else if ((eventSearch.getUsers() == null) && (eventSearch.getCategories() == null)) {
            return getEventsOnlyWithTimeAndStates(eventSearch);
        }
        return setCommentsForEvents(repository.findAllWithAllParameters(eventSearch.getUsers(), eventSearch.getStates(),
                        eventSearch.getCategories(), eventSearch.getRangeStart(), eventSearch.getRangeEnd(),
                        eventSearch.getPageable())
                .stream()
                .map(EventMapper::makeEventFullDto)
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsFromPublicController(EventPublicSearch eventSearch) {
//        eventSearch.setText(Optional.of("%" + eventSearch.getText() + "%").orElse("%%"));
//        eventSearch.setRangeStart(Optional.ofNullable(eventSearch.getRangeStart())
//                .orElse(Timestamp.valueOf(LocalDateTime.now())));
//        eventSearch.setRangeEnd(Optional.ofNullable(eventSearch.getRangeEnd())
//                .orElse(Timestamp.valueOf(LocalDateTime.now().plusYears(100))));
//        if (!(eventSearch.getCategories() == null)) {
//
//        }

        if ((eventSearch.getText() != null) && (eventSearch.getCategories() != null) && (eventSearch.getPaid() != null)
                && (eventSearch.getRangeStart() != null) && (eventSearch.getRangeEnd() != null)
                && (eventSearch.getSort() != null)) {
            String pattern = "%" + eventSearch.getText() + "%";
            if (eventSearch.getSort().equals(SortVariants.EVENT_DATE)) {
                return getEventSortedByEventDate(pattern, eventSearch.getCategories(), eventSearch.getPaid(),
                        eventSearch.getRangeStart(), eventSearch.getRangeEnd(), eventSearch.getOnlyAvailable(),
                        eventSearch.getPageable());
            } else {
                return getEventSortedByViews(pattern, eventSearch.getCategories(), eventSearch.getPaid(),
                        eventSearch.getRangeStart(), eventSearch.getRangeEnd(), eventSearch.getOnlyAvailable(),
                        eventSearch.getPageable());
            }
        } else {
            return repository.findAll().stream().map(EventMapper::makeEventShortDto).collect(Collectors.toList());
        }
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

    // Обновление события
    private void updateEvent(Event event, UpdateEventRequest updateEvent) {
        Category category = checkCategory(updateEvent.getCategory());
        event.setAnnotation(Optional.ofNullable(updateEvent.getAnnotation()).orElse(event.getAnnotation()));
        event.setCategory(category);
        event.setDescription(Optional.ofNullable(updateEvent.getDescription()).orElse(event.getDescription()));
        event.setPaid(Optional.ofNullable(updateEvent.getPaid()).orElse(event.isPaid()));
        event.setParticipantLimit(Optional.ofNullable(updateEvent.getParticipantLimit()).orElse(event.getParticipantLimit()));
        event.setTitle(Optional.ofNullable(updateEvent.getTitle()).orElse(event.getTitle()));
    }

    // Поиск события без списка пользователей
    private List<EventFullDto> getEventsWithoutUsers(EventAdminSearch eventSearch) {
        return setCommentsForEvents(repository.findAllWithoutUsers(eventSearch.getStates(),
                        eventSearch.getCategories(), eventSearch.getRangeStart(), eventSearch.getRangeEnd(),
                        eventSearch.getPageable())
                .stream()
                .map(EventMapper::makeEventFullDto)
                .collect(Collectors.toList()));
    }

    // Поиск события без списка категорий
    private List<EventFullDto> getEventsWithoutCategories(EventAdminSearch eventSearch) {
        return setCommentsForEvents(repository.findAllWithoutCategories(eventSearch.getUsers(),
                        eventSearch.getStates(), eventSearch.getRangeStart(), eventSearch.getRangeEnd(),
                        eventSearch.getPageable())
                .stream()
                .map(EventMapper::makeEventFullDto)
                .collect(Collectors.toList()));
    }

    // Поиск события без списка пользователей и категорий
    private List<EventFullDto> getEventsOnlyWithTimeAndStates(EventAdminSearch eventSearch) {
        return setCommentsForEvents(repository.findAllOnlyWithTimeAndStates(eventSearch.getStates(),
                        eventSearch.getRangeStart(), eventSearch.getRangeEnd(), eventSearch.getPageable())
                .stream()
                .map(EventMapper::makeEventFullDto)
                .collect(Collectors.toList()));
    }

    @Override
    public EventFullDto publishEvent(Long eventId) {
        Event event = getEvent(eventId);
        event.setState(EventStates.PUBLISHED);
        event.setPublishedOn(Timestamp.from(Instant.now()));
        return EventMapper.makeEventFullDto(repository.save(event));
    }

    @Override
    public EventFullDto rejectEvent(Long eventId) {
        Event event = getEvent(eventId);
        event.setState(EventStates.CANCELED);
        return EventMapper.makeEventFullDto(repository.save(event));
    }

    @Override
    public void rejectComment(Long eventId, RejectionCommentRequest commentRequest) {
        Optional<Comment> commentOptional = commentRepository.findById(commentRequest.getId());
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            comment.setStatus(CommentStatus.REJECTED);
            comment.setRejectionReason(commentRequest.getRejectionReason());
            commentRepository.save(comment);
        } else {
            throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The comment object was not found.",
                    String.format("Comment with id=%s was not found.", commentRequest.getId()),
                    LocalDateTime.now());
        }
    }

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

    @Override
    public EventFullDto rejectedEventByInitiator(Long userId, Long eventId) {
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
    public List<ParticipationRequestDto> getRequestsByInitiator(Long userId, Long eventId) {
        Event event = getEvent(eventId);
        checkInitiator(userId, event);
        return requestRepository.findAllByEvent_Id(eventId).stream().map(RequestMapper::makeRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId) {
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
    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId) {
        Event event = getEvent(eventId);
        checkInitiator(userId, event);
        Request request = getRequest(reqId);
        request.setStatus(RequestStates.REJECTED);
        return RequestMapper.makeRequestDto(requestRepository.save(request));
    }

    @Override
    public CommentDto saveNewComment(Long userId, Long eventId, NewCommentDto commentDto) {
        Comment comment = CommentMapper.makeComment(commentDto);
        Optional<Event> eventOptional = repository.findById(eventId);
        if (eventOptional.isPresent()) {
            comment.setEvent(eventOptional.get());
        } else {
            throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The event object was not found.",
                    String.format("Event with id=%s was not found.", eventId),
                    LocalDateTime.now());
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            comment.setUser(userOptional.get());
            comment.setAuthorName(userOptional.get().getName());
        } else {
            throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The user object was not found.",
                    String.format("User with id=%s was not found.", userId),
                    LocalDateTime.now());
        }
        comment.setCreated(Timestamp.from(Instant.now()));
        comment.setStatus(CommentStatus.PUBLISHED);
        return CommentMapper.makeCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, UpdateCommentRequest updateCommentRequest) {
        Optional<Comment> commentOptional = commentRepository.findById(updateCommentRequest.getId());
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
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
            return CommentMapper.makeCommentDto(commentRepository.save(comment));
        }
        throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The comment object was not found.",
                String.format("Comment with id=%s was not found.", updateCommentRequest.getId()),
                LocalDateTime.now());
    }

    @Override
    public List<CommentDto> searchCommentByAuthor(Long userId, Timestamp rangeStart, Timestamp rangeEnd,
                                                  List<CommentStatus> statuses, Integer from, Integer size) {
        if (statuses == null) {
            statuses = new ArrayList<>();
            statuses.add(CommentStatus.REJECTED);
            statuses.add(CommentStatus.PUBLISHED);
        }
        if ((rangeStart == null) || (rangeEnd == null)) {
            return commentRepository.searchCommentByAuthor(userId, rangeStart, rangeEnd, statuses,
                    PageRequest.of(from / size, size)).stream().map(CommentMapper::makeCommentDto)
                    .collect(Collectors.toList());
        }
        return commentRepository.searchCommentByAuthorWithoutTime(userId, statuses,
                        PageRequest.of(from / size, size)).stream().map(CommentMapper::makeCommentDto)
                .collect(Collectors.toList());
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

    private Request getRequest(Long reqId) {
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

    private List<EventShortDto> getEventSortedByEventDate(String pattern, Set<Long> categories, Boolean paid, Timestamp rangeStart,
                                                          Timestamp rangeEnd, Boolean onlyAvailable, Pageable  pageable) {
        if (onlyAvailable) {
            return repository.findAllOrderByEventDateOnlyAvailable(pattern, categories, paid, rangeStart, rangeEnd,
                            LocalDateTime.now(), pageable).stream()
                    .map(EventMapper::makeEventShortDto)
                    .collect(Collectors.toList());
        }
        return repository.findAllOrderByEventDate(pattern, categories, paid, rangeStart, rangeEnd,
                        Timestamp.valueOf(LocalDateTime.now()), pageable).stream()
                .map(EventMapper::makeEventShortDto)
                .collect(Collectors.toList());
    }

    private List<EventShortDto> getEventSortedByViews(String pattern, Set<Long> categories, Boolean paid, Timestamp rangeStart,
                                                      Timestamp rangeEnd, Boolean onlyAvailable, Pageable pageable) {
        if (onlyAvailable) {
            return repository.findAllOrderByViewsOnlyAvailable(pattern, categories, paid, rangeStart, rangeEnd,
                            Timestamp.valueOf(LocalDateTime.now()), pageable).stream()
                    .map(EventMapper::makeEventShortDto)
                    .collect(Collectors.toList());
        }
        return repository.findAllOrderByViews(pattern, categories, paid, rangeStart, rangeEnd,
                        Timestamp.valueOf(LocalDateTime.now()), pageable).stream()
                .map(EventMapper::makeEventShortDto)
                .collect(Collectors.toList());
    }

    private List<EventFullDto> setCommentsForEvents(List<EventFullDto> eventFullDtos) {
        List<Long> eventsIds = eventFullDtos.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<CommentDto> commentDtos = commentRepository.findAllByIds(eventsIds).stream()
                .map(CommentMapper::makeCommentDto).collect(Collectors.toList());
        for (EventFullDto e : eventFullDtos) {
            e.setComments(commentDtos.stream().filter(c -> c.getEventId().equals(e.getId())).collect(Collectors.toList()));
        }
        return eventFullDtos;
    }
}
