package ru.practicum.explore_with_me.event;

import ru.practicum.explore_with_me.event.comment.dto.CommentDto;
import ru.practicum.explore_with_me.event.comment.dto.NewCommentDto;
import ru.practicum.explore_with_me.event.comment.dto.UpdateCommentRequest;
import ru.practicum.explore_with_me.event.comment.dto.UpdatedCommentDto;
import ru.practicum.explore_with_me.event.dto.*;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;

import java.sql.Timestamp;
import java.util.List;

public interface EventService {
    EventFullDto saveNewEvent(long userId, NewEventDto eventDto);

    List<EventFullDto> getEvents(List<Long> users, List<EventStates> states, List<Long> categories, Timestamp rangeStart,
                                 Timestamp rangeEnd, Integer from, Integer size);

    List<EventShortDto> getEventsFromPublicController(String text, List<Long> categories, Boolean paid, Timestamp rangeStart,
                                                      Timestamp rangeEnd, Boolean onlyAvailable, SortVariants sort, Integer from,
                                                      Integer size);

    List<EventShortDto> getEventsByInitiator(long userId, Integer from, Integer size);

    EventFullDto publishEvent(long eventId);

    EventFullDto rejectEvent(long eventId);

    void rejectComment(long eventId, long commentId);

    EventFullDto getEventsByIdFromPublicController(long id);

    EventFullDto getEventsByIdFromPrivateController(long userId, long eventId);

    EventFullDto updateEventByInitiator(long userId, UpdateEventRequest updateEventRequest);

    EventFullDto updateEventByAdmin(long eventId, AdminUpdateEventRequest updateEventRequest);

    EventFullDto rejectedEventByInitiator(long userId, long eventId);

    List<ParticipationRequestDto> getRequestsByInitiator(long userId, long eventId);

    ParticipationRequestDto confirmRequest(long userId, long eventId, long reqId);

    ParticipationRequestDto rejectRequest(long userId, long eventId, long reqId);

    CommentDto saveNewComment(long userId, long eventId, NewCommentDto commentDto);

    UpdatedCommentDto updateComment(long userId, long eventId, UpdateCommentRequest updateCommentRequest);
}
