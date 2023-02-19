package ru.practicum.explore_with_me.event;

import lombok.*;
import ru.practicum.explore_with_me.category.Category;
import ru.practicum.explore_with_me.event.comment.Comment;
import ru.practicum.explore_with_me.request.Request;
import ru.practicum.explore_with_me.user.User;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String annotation;
    @ManyToOne
    private Category category;
    @OneToMany(mappedBy = "event")
    private List<Request> requests;
    private LocalDateTime createdOn;
    private String description;
    private Timestamp eventDate;
    @ManyToOne
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
    @Transient
    private List<Comment> comments;
}
