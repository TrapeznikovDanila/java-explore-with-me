package ru.practicum.explore_with_me.request;

import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;

@Component
public class RequestMapper {

    public static ParticipationRequestDto makeRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus()).build();
    }
}
