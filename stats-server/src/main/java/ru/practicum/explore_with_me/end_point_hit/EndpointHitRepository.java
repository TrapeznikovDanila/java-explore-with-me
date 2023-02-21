package ru.practicum.explore_with_me.end_point_hit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT DISTINCT (e.ip) " +
            "FROM EndpointHit e " +
            "WHERE e.uri IN :uris " +
            "AND e.timestamp BETWEEN :start AND :end")
    List<EndpointHit> findAllIpUnique(Timestamp start, Timestamp end, List<String> uris);

    @Query("SELECT e " +
            "FROM EndpointHit e " +
            "WHERE e.uri IN :uris " +
            "AND e.timestamp BETWEEN :start AND :end")
    List<EndpointHit> findAll(Timestamp start, Timestamp end, List<String> uris);

}
