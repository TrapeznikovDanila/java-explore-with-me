package ru.practicum.exploreWithMe.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByEvent_Id(long eventId);

    List<Request> findAllByRequester_Id(long requesterId);
}
