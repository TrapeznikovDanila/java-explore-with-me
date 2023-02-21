package ru.practicum.explore_with_me.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e " +
            "FROM Event e")
    Page<Event> findAll(Pageable pageable);

    Page<Event> findAllByInitiator_Id(Long initiatorId, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE ((:users) IS NULL OR e.initiator.id IN :users) " +
            "AND ((:states) IS NULL OR e.state IN :states) " +
            "AND ((:categories) IS NULL OR e.category.id IN :categories) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "ORDER BY e.id")
    Page<Event> getEventsByAdmin(Iterable<Long> users, Iterable<EventStates> states, Iterable<Long> categories,
                                 Timestamp rangeStart, Timestamp rangeEnd, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "AND ((:categories) IS NULL OR e.category.id IN :categories) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND ((:paid) IS NULL OR e.paid = :paid) " +
            "AND e.publishedOn > :now " +
            "AND e.participantLimit > (SELECT COUNT(r.id) " +
            "FROM Request r " +
            "WHERE r.status = 'CONFIRMED' " +
            "AND r.event.id = e.id " +
            "GROUP BY r.id) " +
            "ORDER BY e.views")
    Page<Event> getOnlyAvailableEventsOrderByViews(String text, Set<Long> categories, Boolean paid, Timestamp rangeStart,
                          Timestamp rangeEnd, Timestamp now, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "AND ((:categories) IS NULL OR e.category.id IN :categories) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND ((:paid) IS NULL OR e.paid = :paid) " +
            "AND e.publishedOn > :now " +
            "AND e.participantLimit > (SELECT COUNT(r.id) " +
            "FROM Request r " +
            "WHERE r.status = 'CONFIRMED' " +
            "AND r.event.id = e.id " +
            "GROUP BY r.id) " +
            "ORDER BY e.eventDate")
    Page<Event> getOnlyAvailableEventsOrderByEventDate(String text, Set<Long> categories, Boolean paid, Timestamp rangeStart,
                                                   Timestamp rangeEnd, Timestamp now, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "AND ((:categories) IS NULL OR e.category.id IN :categories) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND ((:paid) IS NULL OR e.paid = :paid) " +
            "AND e.publishedOn > :now " +
            "ORDER BY e.views")
    Page<Event> getEventsOrderByViews(String text, Set<Long> categories, Boolean paid, Timestamp rangeStart,
                                                   Timestamp rangeEnd, Timestamp now, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "AND ((:categories) IS NULL OR e.category.id IN :categories) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND ((:paid) IS NULL OR e.paid = :paid) " +
            "AND e.publishedOn > :now " +
            "ORDER BY e.eventDate")
    Page<Event> getEventsOrderByEventDate(String text, Set<Long> categories, Boolean paid, Timestamp rangeStart,
                                                       Timestamp rangeEnd, Timestamp now, Pageable pageable);

    @Query("select e from Event e where e.id in :ids")
    List<Event> findAllByIds(Iterable<Long> ids);
}
