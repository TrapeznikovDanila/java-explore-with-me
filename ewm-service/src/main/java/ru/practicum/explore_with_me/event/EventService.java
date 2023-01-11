package ru.practicum.explore_with_me.event;

import ru.practicum.explore_with_me.event.comment.CommentStatus;
import ru.practicum.explore_with_me.event.comment.dto.CommentDto;
import ru.practicum.explore_with_me.event.comment.dto.NewCommentDto;
import ru.practicum.explore_with_me.event.comment.dto.RejectionCommentRequest;
import ru.practicum.explore_with_me.event.comment.dto.UpdateCommentRequest;
import ru.practicum.explore_with_me.event.dto.*;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;

import java.sql.Timestamp;
import java.util.List;

public interface EventService {
    EventFullDto saveNewEvent(Long userId, NewEventDto eventDto);

    List<EventFullDto> getEvents(List<Long> users, List<EventStates> states, List<Long> categories, Timestamp rangeStart,
                                 Timestamp rangeEnd, Integer from, Integer size);

    List<EventShortDto> getEventsFromPublicController(String text, List<Long> categories, Boolean paid, Timestamp rangeStart,
                                                      Timestamp rangeEnd, Boolean onlyAvailable, SortVariants sort, Integer from,
                                                      Integer size);

    List<EventShortDto> getEventsByInitiator(Long userId, Integer from, Integer size);

    EventFullDto publishEvent(Long eventId);

    EventFullDto rejectEvent(Long eventId);

    void rejectComment(Long eventId, RejectionCommentRequest commentRequest);

    EventFullDto getEventsByIdFromPublicController(Long id);

    EventFullDto getEventsByIdFromPrivateController(Long userId, Long eventId);

    EventFullDto updateEventByInitiator(Long userId, UpdateEventRequest updateEventRequest);

    EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventRequest updateEventRequest);

    EventFullDto rejectedEventByInitiator(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByInitiator(Long userId, Long eventId);

    ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId);

    ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId);

    CommentDto saveNewComment(Long userId, Long eventId, NewCommentDto commentDto);

    CommentDto updateComment(Long userId, Long eventId, UpdateCommentRequest updateCommentRequest);

    List<CommentDto> searchCommentByAuthor(Long userId, Timestamp rangeStart, Timestamp rangeEnd, List<CommentStatus> statuses,
                          Integer from, Integer size);

    List<CommentDto> getComments(List<Long> users, List<Long> events, List<CommentStatus> statuses, Timestamp rangeStart, Timestamp rangeEnd,
                                 Integer from, Integer size);
}
