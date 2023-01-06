package ru.practicum.explore_with_me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explore_with_me.event.Location;

import java.sql.Timestamp;

@Data
public class AdminUpdateEventRequest {
    private String annotation;
    private Long category;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Timestamp eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;
}
