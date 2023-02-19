package ru.practicum.explore_with_me.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventPublicSearch;
import ru.practicum.explore_with_me.event.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventPublicController {

    private final EventService service;
    private final EventClient client;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS") Timestamp rangeStart,
                                         @RequestParam(required = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS") Timestamp rangeEnd,
                                         @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) SortVariants sort,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        EventPublicSearch eventSearch = EventPublicSearch.builder()
                .text(text)
                .categories(new HashSet<>(categories))
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(Optional.ofNullable(sort).orElse(SortVariants.EVENT_DATE))
                .pageable(PageRequest.of(from / size, size))
                .build();
        client.saveStats(request);
        return service.getEventsFromPublicController(eventSearch);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventsById(@PathVariable Long id, HttpServletRequest request) {
        client.saveStats(request);
        return service.getEventsByIdFromPublicController(id);
    }
}
