package ru.practicum.explore_with_me.event.comment;

import lombok.Data;
import ru.practicum.explore_with_me.event.Event;
import ru.practicum.explore_with_me.user.User;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "comments")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String authorName;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    private String text;
    private Timestamp created;
    private Timestamp updated;
    @Enumerated(EnumType.STRING)
    private CommentStatus status;
    private String rejectionReason;
}
