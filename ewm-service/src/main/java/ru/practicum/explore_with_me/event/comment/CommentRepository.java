package ru.practicum.explore_with_me.event.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.user.id = :userId " +
            "AND c.status IN :statuses " +
            "AND c.created BETWEEN :rangeStart AND :rangeEnd")
    Page<Comment> findCommentsByAuthor(long userId, List<CommentStatus> statuses, Timestamp rangeStart,
                                       Timestamp rangeEnd, Pageable pageable);

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.event.id IN :eventsIds " +
            "AND c.status = 'PUBLISHED'")
    List<Comment> findAllByIds(Iterable<Long> eventsIds);

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.event.id = :eventId " +
            "AND c.status = 'PUBLISHED'")
    List<Comment> findAllByEventId(Long eventId);

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.user.id = :userId " +
            "AND c.status IN :statuses " +
            "AND c.created BETWEEN :rangeStart AND :rangeEnd")
    Page<Comment> searchCommentByAuthor(Long userId, Timestamp rangeStart, Timestamp rangeEnd, List<CommentStatus> statuses, Pageable pageable);

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.user.id = :userId " +
            "AND c.status IN :statuses")
    Page<Comment> searchCommentByAuthorWithoutTime(Long userId, List<CommentStatus> statuses, Pageable pageable);

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.user.id IN :users " +
            "AND c.event.id IN :events " +
            "AND c.status IN :statuses " +
            "AND c.created BETWEEN :rangeStart AND :rangeEnd")
    Page<Comment> searchCommentByAdmin(Iterable<Long> users, Iterable<Long> events, Timestamp rangeStart, Timestamp rangeEnd,
                                       List<CommentStatus> statuses, Pageable pageable);

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.user.id IN :users " +
            "AND c.status IN :statuses " +
            "AND c.created BETWEEN :rangeStart AND :rangeEnd")
    Page<Comment> searchCommentByAdminWithoutEvents(Iterable<Long> users, Timestamp rangeStart, Timestamp rangeEnd,
                                                    List<CommentStatus> statuses, Pageable pageable);

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.event.id IN :events " +
            "AND c.status IN :statuses " +
            "AND c.created BETWEEN :rangeStart AND :rangeEnd")
    Page<Comment> searchCommentByAdminWithoutUsers(Iterable<Long> events, Timestamp rangeStart, Timestamp rangeEnd,
                                                   List<CommentStatus> statuses, Pageable pageable);
}
