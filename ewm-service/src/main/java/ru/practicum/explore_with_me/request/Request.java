package ru.practicum.explore_with_me.request;

import lombok.*;
import ru.practicum.explore_with_me.event.Event;
import ru.practicum.explore_with_me.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Timestamp created;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User requester;
    @Enumerated(EnumType.STRING)
    private RequestStates status;
}
