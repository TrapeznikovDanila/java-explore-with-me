package ru.practicum.explore_with_me.request;

import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;

@Component
public class RequestMapper {

    public static ParticipationRequestDto makeRequestDto(Request request) {
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(request.getId());
        requestDto.setCreated(request.getCreated());
        requestDto.setEvent(request.getEvent().getId());
        requestDto.setRequester(request.getRequester().getId());
        requestDto.setStatus(request.getStatus());
        return requestDto;
    }
}
