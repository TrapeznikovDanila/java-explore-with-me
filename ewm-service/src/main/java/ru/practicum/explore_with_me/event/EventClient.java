package ru.practicum.explore_with_me.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explore_with_me.client.BaseClient;
import ru.practicum.explore_with_me.event.dto.EndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;

@Service
public class EventClient extends BaseClient {

    @Autowired
    public EventClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveStats(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app("main-service")
                .timestamp(Timestamp.from(Instant.now()))
                .build();

        return post("/hit", endpointHitDto);
    }
}
