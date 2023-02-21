package ru.practicum.explore_with_me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.domain.Pageable;
import ru.practicum.explore_with_me.event.EventStates;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EventAdminSearch {
    private Set<Long> users;
    private Set<EventStates> states;
    private Set<Long> categories;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Timestamp rangeStart;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Timestamp rangeEnd;
    private Pageable pageable;
}
