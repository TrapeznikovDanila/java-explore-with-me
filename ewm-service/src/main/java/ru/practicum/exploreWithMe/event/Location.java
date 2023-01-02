package ru.practicum.exploreWithMe.event;

public class Location {
    private String lat;
    private String lon;

    public Location(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}
