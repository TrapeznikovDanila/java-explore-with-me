package ru.practicum.exploreWithMe.EndPointHit;

import ru.practicum.exploreWithMe.EndPointHit.dto.EndpointHitDto;
import ru.practicum.exploreWithMe.EndPointHit.dto.ViewStats;

import java.sql.Timestamp;
import java.util.List;

public interface EndPointHitService {
    void save(EndpointHitDto endpointHitDto);

    List<ViewStats> getStats(Timestamp start, Timestamp end, List<String> uris, Boolean unique);
}
