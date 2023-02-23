package ru.practicum.explore_with_me.end_point_hit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select distinct (e.ip) from EndpointHit e where e.uri in :uris and e.timestamp between :start " +
            "and :end")
    List<EndpointHit> findAllIpUnique(Timestamp start, Timestamp end, List<String> uris);

    @Query("select e from EndpointHit e where e.uri in :uris and e.timestamp between :start " +
            "and :end")
    List<EndpointHit> findAll(Timestamp start, Timestamp end, List<String> uris);

}
