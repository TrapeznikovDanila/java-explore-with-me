package ru.practicum.exploreWithMe.EndPointHit;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.EndPointHit.dto.EndpointHitDto;
import ru.practicum.exploreWithMe.EndPointHit.dto.ViewStats;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class EndPointHitController {

    private final EndPointHitService service;

    @PostMapping("/hit")
    public void save(@RequestBody @Validated EndpointHitDto endpointHitDto) {
        service.save(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS") Timestamp start,
                                    @RequestParam @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS") Timestamp end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(required = false, name = "unique", defaultValue = "false") Boolean unique) {
        return service.getStats(start, end, uris, unique);
    }
}
