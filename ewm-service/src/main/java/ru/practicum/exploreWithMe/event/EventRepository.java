package ru.practicum.exploreWithMe.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e from Event e where e.initiator.id in :users and e.state in :states and e.category.id in :categories" +
            " and e.eventDate between :rangeStart and :rangeEnd")
    Page<Event> findAllWithAllParameters(Iterable<Long> users, Iterable<EventStates> states, Iterable<Long> categories,
                                         Timestamp rangeStart, Timestamp rangeEnd, Pageable pageable);

    @Query("select e from Event e where e.state in :states and e.category.id in :categories" +
            " and e.eventDate between :rangeStart and :rangeEnd")
    Page<Event> findAllWithoutUsers(Iterable<EventStates> states, Iterable<Long> categories,
                                    Timestamp rangeStart, Timestamp rangeEnd, Pageable pageable);

    @Query("select e from Event e where e.initiator.id in :users and e.state in :states and e.eventDate between :rangeStart " +
            "and :rangeEnd")
    Page<Event> findAllWithoutCategories(Iterable<Long> users, Iterable<EventStates> states, Timestamp rangeStart,
                                         Timestamp rangeEnd, Pageable pageable);

    @Query("select e from Event e where e.initiator.id in :users and e.state in :states and e.category.id in :categories")
    Page<Event> findAllWithoutTime(Iterable<Long> users, Iterable<EventStates> states, Iterable<Long> categories,
                                   Pageable pageable);

    @Query("select e from Event e where e.initiator.id in :users and e.state in :states")
    Page<Event> findAllOnlyWithUsersAndStates(Iterable<Long> users, Iterable<EventStates> states, Pageable pageable);

    @Query("select e from Event e where e.state in :states and e.category.id in :categories")
    Page<Event> findAllOnlyWithCategoriesAndStates(Iterable<EventStates> states, Iterable<Long> categories,
                                                   Pageable pageable);

    @Query("select e from Event e where e.state in :states and e.eventDate between :rangeStart and :rangeEnd")
    Page<Event> findAllOnlyWithTimeAndStates(Iterable<EventStates> states, Timestamp rangeStart, Timestamp rangeEnd,
                                             Pageable pageable);

    @Query("select e from Event e")
    Page<Event> findAll(Pageable pageable);

    Page<Event> findAllByInitiator_Id(long initiatorId, Pageable pageable);

    @Query("select e from Event e where e.annotation like :pattern or e.description like :pattern and e.category.id in :categories" +
            " and e.eventDate between :rangeStart and :rangeEnd and e.paid = :paid and e.publishedOn > :now order by e.eventDate")
    Page<Event> findAllOrderByEventDate(String pattern, List<Long> categories, Boolean paid, Timestamp rangeStart,
                                        Timestamp rangeEnd, Timestamp now, Pageable pageable);

    @Query("select e from Event e where e.annotation like :pattern or e.description like :pattern and e.category.id in :categories" +
            " and e.eventDate between :rangeStart and :rangeEnd and e.participantLimit > e.confirmedRequests or e.participantLimit = 0 " +
            "and e.paid = :paid and e.publishedOn > :now order by e.eventDate")
    Page<Event> findAllOrderByEventDateOnlyAvailable(String pattern, List<Long> categories, Boolean paid, Timestamp rangeStart,
                                                     Timestamp rangeEnd, LocalDateTime now, Pageable pageable);

    @Query("select e from Event e where e.annotation like :pattern or e.description like :pattern and e.category.id in :categories" +
            " and e.eventDate between :rangeStart and :rangeEnd and e.paid = :paid and e.publishedOn > :now order by e.views")
    Page<Event> findAllOrderByViews(String pattern, List<Long> categories, Boolean paid, Timestamp rangeStart,
                                    Timestamp rangeEnd, Timestamp now, Pageable pageable);

    @Query("select e from Event e where e.annotation like :pattern or e.description like :pattern and e.category.id in :categories" +
            " and e.eventDate between :rangeStart and :rangeEnd and e.participantLimit > e.confirmedRequests or e.participantLimit = 0 " +
            "and e.paid = :paid and e.publishedOn > :now order by e.views")
    Page<Event> findAllOrderByViewsOnlyAvailable(String pattern, List<Long> categories, Boolean paid, Timestamp rangeStart,
                                                 Timestamp rangeEnd, Timestamp now, Pageable pageable);
}
