package ru.practicum.explore_with_me.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explore_with_me.request.RequestStates;

import java.sql.Timestamp;

@Data
public class ParticipationRequestDto {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Timestamp created;
    private Long event;
    private Long requester;
    private RequestStates status;
}
