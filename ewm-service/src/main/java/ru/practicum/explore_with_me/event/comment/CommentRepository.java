package ru.practicum.explore_with_me.event.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.eventId in :eventsIds and c.status = 'PUBLISHED'")
    List<Comment> findAllByIds(Iterable<Long> eventsIds);

    @Query("select c from Comment c where c.eventId = :eventId and c.status = 'PUBLISHED'")
    List<Comment> findAllByEventId(long eventId);
}
