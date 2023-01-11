package ru.practicum.explore_with_me.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class RequestPrivateController {

    private final RequestService service;

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto saveNewEvent(@PathVariable Long userId,
                                                @RequestParam(name = "eventId") Long eventId) {
        return service.saveNewRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestByUserId(@PathVariable Long userId) {
        return service.getRequestByUserId(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto canceledRequestByRequester(@PathVariable Long userId, @PathVariable Long requestId) {
        return service.canceledRequestByRequester(userId, requestId);
    }
}
