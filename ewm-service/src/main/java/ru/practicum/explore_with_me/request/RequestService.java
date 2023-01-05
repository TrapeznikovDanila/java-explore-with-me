package ru.practicum.explore_with_me.request;

import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto saveNewRequest(long userId, long eventId);

    List<ParticipationRequestDto> getRequestByUserId(long userId);

    ParticipationRequestDto canceledRequestByRequester(long userId, long requestId);
}
