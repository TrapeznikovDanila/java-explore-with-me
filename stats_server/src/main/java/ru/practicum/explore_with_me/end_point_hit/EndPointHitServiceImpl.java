package ru.practicum.explore_with_me.end_point_hit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.end_point_hit.dto.EndpointHitDto;
import ru.practicum.explore_with_me.end_point_hit.dto.ViewStats;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EndPointHitServiceImpl implements EndPointHitService {

    private final EndpointHitRepository repository;

    @Override
    public void save(EndpointHitDto endpointHitDto) {
        repository.save(EndpointHitMapper.makeEndpointHit(endpointHitDto));
    }

    @Override
    public List<ViewStats> getStats(Timestamp start, Timestamp end, List<String> uris, Boolean unique) {
        if (unique) {
            return getStatsIpUnique(start, end, uris);
        }
        List<EndpointHit> endpointHits = repository.findAll(start, end, uris);
        List<ViewStats> viewStatsList = new ArrayList<>();
        if (endpointHits.size() > 0) {
            for (String uri : uris) {
                ViewStats viewStats = new ViewStats();
                viewStats.setApp(endpointHits.get(0).getApp());
                viewStats.setUri(uri);
                viewStats.setHits(endpointHits.stream().filter(endpointHit -> endpointHit.getUri().equals(uri))
                        .collect(Collectors.toList()).size());
                viewStatsList.add(viewStats);
            }
            return viewStatsList;
        }
        return null;
    }

    private List<ViewStats> getStatsIpUnique(Timestamp start, Timestamp end, List<String> uris) {
        List<EndpointHit> endpointHits = repository.findAllIpUnique(start, end, uris);
        List<ViewStats> viewStatsList = new ArrayList<>();
        for (String uri : uris) {
            ViewStats viewStats = new ViewStats();
            viewStats.setApp(endpointHits.get(0).getApp());
            viewStats.setUri(uri);
            viewStats.setHits(endpointHits.stream().filter(endpointHit -> endpointHit.getUri() == uri)
                    .collect(Collectors.toList()).size());
            viewStatsList.add(viewStats);
        }
        return viewStatsList;
    }
}
