package ru.practicum.explore_with_me.event.comment;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "comments")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    private String authorName;
    private long eventId;
    private String text;
    private Timestamp created;
    @Enumerated(EnumType.STRING)
    private CommentStatus status;
}
