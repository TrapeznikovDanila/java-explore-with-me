package ru.practicum.explore_with_me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explore_with_me.event.Location;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Data
public class NewEventDto {
    @NotNull
    @Size(max = 2000, min = 20)
    private String annotation;
    @NotNull
    private long category;
    @NotNull
    @Size(max = 7000, min = 20)
    private String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Timestamp eventDate;
    @NotNull
    private Location location;
    private boolean paid;
    private Integer participantLimit;
    private boolean requestModeration;
    @NotNull
    @Size(max = 120, min = 3)
    private String title;
}
