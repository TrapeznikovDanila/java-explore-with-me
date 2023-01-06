package ru.practicum.explore_with_me.end_point_hit.dto;

import lombok.Data;

@Data
public class ViewStats {
    private String app;
    private String uri;
    private long hits;
}
