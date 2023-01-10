package ru.practicum.explore_with_me.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.event.comment.CommentStatus;
import ru.practicum.explore_with_me.event.comment.dto.CommentDto;
import ru.practicum.explore_with_me.event.comment.dto.RejectionCommentRequest;
import ru.practicum.explore_with_me.event.dto.AdminUpdateEventRequest;
import ru.practicum.explore_with_me.event.dto.EventFullDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class EventsAdminController {

    private final EventService service;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<EventStates> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS") Timestamp rangeStart,
                                        @RequestParam(required = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS") Timestamp rangeEnd,
                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return service.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable long eventId) {
        return service.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable long eventId) {
        return service.rejectEvent(eventId);
    }

    @PutMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable long eventId, @RequestBody AdminUpdateEventRequest eventRequest) {
        return service.updateEventByAdmin(eventId, eventRequest);
    }

    @PatchMapping("/{eventId}/comment/reject")
    public void rejectComment(@PathVariable long eventId, @RequestBody @Valid RejectionCommentRequest commentRequest) {
        service.rejectComment(eventId, commentRequest);
    }

    @GetMapping("/comments")
    public List<CommentDto> getComments(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<Long> events,
                                        @RequestParam(required = false) List<CommentStatus> statuses,
                                        @RequestParam(required = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS") Timestamp rangeStart,
                                        @RequestParam(required = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS") Timestamp rangeEnd,
                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return service.getComments(users, events, statuses, rangeStart, rangeEnd, from, size);
    }
}
