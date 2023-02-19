package ru.practicum.explore_with_me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndpointHitDto {
    private String app;
    private String uri;
    private String ip;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Timestamp timestamp;
}
