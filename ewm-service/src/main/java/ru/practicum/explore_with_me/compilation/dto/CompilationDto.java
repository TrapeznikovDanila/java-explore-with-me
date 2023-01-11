package ru.practicum.explore_with_me.compilation.dto;

import lombok.Data;
import ru.practicum.explore_with_me.event.dto.EventShortDto;

import java.util.List;

@Data
public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    private boolean pinned;
    private String title;
}
