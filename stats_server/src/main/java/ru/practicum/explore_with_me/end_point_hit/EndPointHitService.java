package ru.practicum.explore_with_me.end_point_hit;

import ru.practicum.explore_with_me.end_point_hit.dto.EndpointHitDto;
import ru.practicum.explore_with_me.end_point_hit.dto.ViewStats;

import java.sql.Timestamp;
import java.util.List;

public interface EndPointHitService {
    void save(EndpointHitDto endpointHitDto);

    List<ViewStats> getStats(Timestamp start, Timestamp end, List<String> uris, Boolean unique);
}
