package ru.practicum.explore_with_me.event.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventRequest {
    @Size(max = 2000, min = 20)
    private String annotation;
    private Long category;
    @Size(max = 7000, min = 20)
    private String description;
    @NotNull
    private Long eventId;
    private Boolean paid;
    private Integer participantLimit;
    @Size(max = 120, min = 3)
    private String title;
}
