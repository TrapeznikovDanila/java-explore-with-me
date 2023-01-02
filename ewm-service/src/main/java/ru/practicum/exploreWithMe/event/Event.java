package ru.practicum.exploreWithMe.event;

import lombok.Data;
import ru.practicum.exploreWithMe.category.Category;
import ru.practicum.exploreWithMe.user.User;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;
    private int confirmedRequests;
    private LocalDateTime createdOn;
    private String description;
    private Timestamp eventDate;
    @ManyToOne
    @JoinColumn(name = "INITIATOR_ID")
    private User initiator;
    private String locationLat;
    private String locationLon;
    private boolean paid;
    private int participantLimit;
    private Timestamp publishedOn;
    private boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private EventStates state;
    private String title;
    private long views;


}
