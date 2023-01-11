package ru.practicum.explore_with_me.event.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.event.id in :eventsIds and c.status = 'PUBLISHED'")
    List<Comment> findAllByIds(Iterable<Long> eventsIds);

    @Query("select c from Comment c where c.event.id = :eventId and c.status = 'PUBLISHED'")
    List<Comment> findAllByEventId(Long eventId);

    @Query("select c from Comment c where c.user.id = :userId and c.status in :statuses and c.created between :rangeStart " +
            "and :rangeEnd")
    Page<Comment> searchCommentByAuthor(Long userId, Timestamp rangeStart, Timestamp rangeEnd, List<CommentStatus> statuses, Pageable pageable);

    @Query("select c from Comment c where c.user.id = :userId and c.status in :statuses")
    Page<Comment> searchCommentByAuthorWithoutTime(Long userId, List<CommentStatus> statuses, Pageable pageable);

    @Query("select c from Comment c where c.user.id in :users and c.event.id in :events and c.status in :statuses " +
            "and c.created between :rangeStart and :rangeEnd")
    Page<Comment> searchCommentByAdmin(Iterable<Long> users, Iterable<Long> events, Timestamp rangeStart, Timestamp rangeEnd,
                                        List<CommentStatus> statuses, Pageable pageable);

    @Query("select c from Comment c where c.user.id in :users and c.status in :statuses and c.created between :rangeStart " +
            "and :rangeEnd")
    Page<Comment> searchCommentByAdminWithoutEvents(Iterable<Long> users, Timestamp rangeStart, Timestamp rangeEnd,
                                        List<CommentStatus> statuses, Pageable pageable);

    @Query("select c from Comment c where c.event.id in :events and c.status in :statuses and c.created between " +
            ":rangeStart and :rangeEnd")
    Page<Comment> searchCommentByAdminWithoutUsers(Iterable<Long> events, Timestamp rangeStart, Timestamp rangeEnd,
                                        List<CommentStatus> statuses, Pageable pageable);
}
