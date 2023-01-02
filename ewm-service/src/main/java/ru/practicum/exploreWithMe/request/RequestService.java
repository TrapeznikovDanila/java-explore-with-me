package ru.practicum.exploreWithMe.request;

import ru.practicum.exploreWithMe.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto saveNewRequest(long userId, long eventId);

    List<ParticipationRequestDto> getRequestByUserId(long userId);

    ParticipationRequestDto canceledRequestByRequester(long userId, long requestId);
}
