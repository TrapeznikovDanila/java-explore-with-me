package ru.practicum.explore_with_me.end_point_hit;

import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.end_point_hit.dto.EndpointHitDto;

@Component
public class EndpointHitMapper {

    public static EndpointHit makeEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(endpointHitDto.getApp());
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setIp(endpointHitDto.getIp());
        endpointHit.setTimestamp(endpointHitDto.getTimestamp());
        return endpointHit;
    }
}
