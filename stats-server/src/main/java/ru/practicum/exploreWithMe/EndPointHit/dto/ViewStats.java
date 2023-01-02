package ru.practicum.exploreWithMe.EndPointHit.dto;

import lombok.Data;

@Data
public class ViewStats {
    private String app;
    private String uri;
    private long hits;
}
