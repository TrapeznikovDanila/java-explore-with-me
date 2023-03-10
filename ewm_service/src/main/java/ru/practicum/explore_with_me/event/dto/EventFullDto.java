package ru.practicum.explore_with_me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.event.EventStates;
import ru.practicum.explore_with_me.event.Location;
import ru.practicum.explore_with_me.user.dto.UserShortDto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Timestamp eventDate;
    private long id;
    private UserShortDto initiator;
    private Location location;
    private boolean paid;
    private int participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Timestamp publishedOn;
    private boolean requestModeration;
    private EventStates state;
    private String title;
    private long views;

}
