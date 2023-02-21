package ru.practicum.explore_with_me.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.dto.NewCompilationDto;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {

    private final CompilationService service;

    @PostMapping
    public CompilationDto saveNewCategory(@RequestBody @Validated NewCompilationDto compilationDto) {
        return service.saveNewCompilation(compilationDto);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        service.deleteCompilation(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable Long compId, @PathVariable Long eventId) {
        service.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEventToCollection(@PathVariable Long compId, @PathVariable Long eventId) {
        service.addEventToCollection(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public void unpinnedCompilation(@PathVariable Long compId) {
        service.unpinnedCompilation(compId);
    }

    @PatchMapping("/{compId}/pin")
    public void pinnedCompilation(@PathVariable Long compId) {
        service.pinnedCompilation(compId);
    }
}
