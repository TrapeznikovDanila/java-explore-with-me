package ru.practicum.explore_with_me.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByEvent_Id(Long eventId);

    List<Request> findAllByRequester_Id(Long requesterId);
}
