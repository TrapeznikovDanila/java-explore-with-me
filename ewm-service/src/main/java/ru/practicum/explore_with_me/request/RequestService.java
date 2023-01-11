package ru.practicum.explore_with_me.request;

import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto saveNewRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestByUserId(Long userId);

    ParticipationRequestDto canceledRequestByRequester(Long userId, Long requestId);
}
