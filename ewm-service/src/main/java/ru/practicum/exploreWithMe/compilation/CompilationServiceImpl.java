package ru.practicum.exploreWithMe.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.exploreWithMe.compilation.dto.NewCompilationDto;
import ru.practicum.exploreWithMe.event.Event;
import ru.practicum.exploreWithMe.event.EventRepository;
import ru.practicum.exploreWithMe.exception.ErrorStatus;
import ru.practicum.exploreWithMe.exception.NotFoundException;
import ru.practicum.exploreWithMe.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository repository;

    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        if (pinned == null) {
            return getCompilations(from, size);
        }
        return repository.findAll(pinned, PageRequest.of(from / size, size))
                .stream()
                .map(CompilationMapper::makeCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto saveNewCompilation(NewCompilationDto compilationDto) {
        List<Event> events = eventRepository.findAll().stream()
                .filter(event -> (compilationDto.getEvents().contains(event.getId()))).collect(Collectors.toList());
        Compilation compilation = CompilationMapper.makeCompilation(compilationDto);
        compilation.setEvents(events);
        return CompilationMapper.makeCompilationDto(repository.save(compilation));
    }

    @Override
    public CompilationDto getCompilationByIdFromPublicController(long compId) {
        return CompilationMapper.makeCompilationDto(getCompilation(compId));
    }

    @Override
    public void deleteCompilation(long compId) {
        getCompilation(compId);
        repository.deleteById(compId);
    }

    @Override
    public void deleteEventFromCompilation(long compId, long eventId) {
        Compilation compilation = getCompilation(compId);
        List<Long> eventsIds = compilation.getEvents().stream().map(c -> c.getId()).collect(Collectors.toList());
        if (eventsIds.contains(eventId)) {
            List<Event> events = compilation.getEvents().stream().filter(e -> (e.getId() != eventId))
                    .collect(Collectors.toList());
            compilation.setEvents(events);
            repository.save(compilation);
        } else {
            throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The event is not in the compilation.",
                    String.format("Event with id=%s1 is not in the compilation with id=%s2.", eventId, compId),
                    LocalDateTime.now());
        }
    }

    @Override
    public void addEventToCollection(long compId, long eventId) {
        Compilation compilation = getCompilation(compId);
        Event event = getEvent(eventId);
        compilation.getEvents().add(event);
        repository.save(compilation);
    }

    @Override
    public void unpinnedCompilation(long compId) {
        Compilation compilation = getCompilation(compId);
        if (compilation.isPinned()) {
            compilation.setPinned(false);
            repository.save(compilation);
        } else {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "Compilation was already unpinned.",
                    String.format("Compilation with id=%s was already unpinned.", compId),
                    LocalDateTime.now());
        }
    }

    @Override
    public void pinnedCompilation(long compId) {
        Compilation compilation = getCompilation(compId);
        if (!compilation.isPinned()) {
            compilation.setPinned(true);
            repository.save(compilation);
        } else {
            throw new ValidationException(null, ErrorStatus.CONFLICT, "Compilation was already pinned.",
                    String.format("Compilation with id=%s was already pinned.", compId),
                    LocalDateTime.now());
        }
    }

    private Compilation getCompilation(long compId) {
        Optional<Compilation> compilationOptional = repository.findById(compId);
        if (compilationOptional.isPresent()) {
            return compilationOptional.get();
        }
        throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The compilation object was not found.",
                String.format("Compilation with id=%s was not found.", compId),
                LocalDateTime.now());
    }

    private Event getEvent(long eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            return eventOptional.get();

        }
        throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The event object was not found.",
                String.format("Event with id=%s was not found.", eventId),
                LocalDateTime.now());
    }

    private List<CompilationDto> getCompilations(int from, int size) {
        return repository.findAll(PageRequest.of(from / size, size))
                .stream()
                .map(CompilationMapper::makeCompilationDto)
                .collect(Collectors.toList());
    }
}
