package ru.practicum.explore_with_me.compilation;

import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto saveNewCompilation(NewCompilationDto compilationDto);

    CompilationDto getCompilationByIdFromPublicController(Long compId);

    void deleteCompilation(Long compId);

    void deleteEventFromCompilation(Long compId, Long eventId);

    void addEventToCollection(Long compId, Long eventId);

    void unpinnedCompilation(Long compId);

    void pinnedCompilation(Long compId);
}
