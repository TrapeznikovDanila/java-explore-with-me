package ru.practicum.explore_with_me.compilation;

import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto saveNewCompilation(NewCompilationDto compilationDto);

    CompilationDto getCompilationByIdFromPublicController(long compId);

    void deleteCompilation(long compId);

    void deleteEventFromCompilation(long compId, long eventId);

    void addEventToCollection(long compId, long eventId);

    void unpinnedCompilation(long compId);

    void pinnedCompilation(long compId);
}
