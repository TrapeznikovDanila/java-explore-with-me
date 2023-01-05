package ru.practicum.explore_with_me.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Location {
    private final String lat;
    private final String lon;
}
