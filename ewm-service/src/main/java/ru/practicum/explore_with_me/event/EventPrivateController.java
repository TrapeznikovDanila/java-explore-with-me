package ru.practicum.explore_with_me.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.event.comment.CommentStatus;
import ru.practicum.explore_with_me.event.comment.dto.CommentDto;
import ru.practicum.explore_with_me.event.comment.dto.NewCommentDto;
import ru.practicum.explore_with_me.event.comment.dto.UpdateCommentRequest;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventRequest;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class EventPrivateController {

    private final EventService service;

    @PostMapping("/{userId}/events")
    public EventFullDto saveNewEvent(@RequestBody @Validated NewEventDto eventDto, @PathVariable Long userId) {
        return service.saveNewEvent(userId, eventDto);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvents(@PathVariable Long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return service.getEventsByInitiator(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventsById(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getEventsByIdFromPrivateController(userId, eventId);
    }

    @PatchMapping("/{userId}/events")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @RequestBody @Validated UpdateEventRequest updateEventRequest) {
        return service.updateEventByInitiator(userId, updateEventRequest);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto rejectedEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.rejectedEventByInitiator(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByInitiator(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getRequestsByInitiator(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequest(@PathVariable Long userId, @PathVariable Long eventId,
                                                  @PathVariable Long reqId) {
        return service.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequest(@PathVariable Long userId, @PathVariable Long eventId,
                                                 @PathVariable Long reqId) {
        return service.rejectRequest(userId, eventId, reqId);
    }

    @PostMapping("/{userId}/events/{eventId}/comment")
    public CommentDto saveNewComment(@PathVariable Long userId, @PathVariable Long eventId,
                                     @RequestBody @Valid NewCommentDto commentDto) {
        return service.saveNewComment(userId, eventId, commentDto);
    }

    @PatchMapping("/{userId}/events/{eventId}/comment")
    public CommentDto updateComment(@PathVariable Long userId, @PathVariable Long eventId,
                                           @RequestBody @Validated UpdateCommentRequest updateCommentRequest) {
        return service.updateComment(userId, eventId, updateCommentRequest);
    }

    @GetMapping("/{userId}/comments")
    public List<CommentDto> searchCommentByAuthor(@PathVariable Long userId,
                                                  @RequestParam(required = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS") Timestamp rangeStart,
                                                  @RequestParam(required = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS") Timestamp rangeEnd,
                                                  @RequestParam(required = false) List<CommentStatus> statuses,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return service.searchCommentByAuthor(userId, rangeStart, rangeEnd, statuses, from, size);
    }
}
