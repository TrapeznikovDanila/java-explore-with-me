package ru.practicum.exploreWithMe.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.event.dto.EventFullDto;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;
import ru.practicum.exploreWithMe.event.dto.NewEventDto;
import ru.practicum.exploreWithMe.event.dto.UpdateEventRequest;
import ru.practicum.exploreWithMe.request.dto.ParticipationRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class EventPrivateController {

    private final EventService service;

    @PostMapping("/{userId}/events")
    public EventFullDto saveNewEvent(@RequestBody @Validated NewEventDto eventDto, @PathVariable long userId) {
        return service.saveNewEvent(userId, eventDto);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvents(@PathVariable long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return service.getEventsByInitiator(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventsById(@PathVariable long userId, @PathVariable long eventId) {
        return service.getEventsByIdFromPrivateController(userId, eventId);
    }

    @PatchMapping("/{userId}/events")
    public EventFullDto updateEvent(@PathVariable long userId,
                                    @RequestBody @Validated UpdateEventRequest updateEventRequest) {
        return service.updateEventByInitiator(userId, updateEventRequest);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto rejectedEvent(@PathVariable long userId, @PathVariable long eventId) {
        return service.rejectedEventByInitiator(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByInitiator(@PathVariable long userId, @PathVariable long eventId) {
        return service.getRequestsByInitiator(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequest(@PathVariable long userId, @PathVariable long eventId,
                                                  @PathVariable long reqId) {
        return service.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequest(@PathVariable long userId, @PathVariable long eventId,
                                                 @PathVariable long reqId) {
        return service.rejectRequest(userId, eventId, reqId);
    }
}
