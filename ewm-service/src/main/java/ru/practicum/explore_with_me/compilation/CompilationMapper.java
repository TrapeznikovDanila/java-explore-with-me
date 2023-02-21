package ru.practicum.explore_with_me.compilation;

import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.dto.NewCompilationDto;
import ru.practicum.explore_with_me.event.EventMapper;

import java.util.stream.Collectors;

@Component
public class CompilationMapper {

    public static Compilation makeCompilation(NewCompilationDto compilationDto) {
        return Compilation.builder()
                .pinned(compilationDto.isPinned())
                .title(compilationDto.getTitle()).build();
    }

    public static CompilationDto makeCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream().map(EventMapper::makeEventShortDto)
                        .collect(Collectors.toList()))
                .pinned(compilation.isPinned())
                .title(compilation.getTitle()).build();
    }
}
