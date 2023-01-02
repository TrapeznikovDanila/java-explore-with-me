package ru.practicum.exploreWithMe.compilation.dto;

import lombok.Data;
import ru.practicum.exploreWithMe.event.dto.EventShortDto;

import java.util.List;

@Data
public class CompilationDto {
    private long id;
    private List<EventShortDto> events;
    private boolean pinned;
    private String title;
}
