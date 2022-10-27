package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.StatHits;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = "select count(s.id) as hits, s.app as app, s.uri as uri from stats s where " +
            "(s.timestamp between :startTime and :endTime) and (s.uri = :uri) group by s.app, s.uri",
            nativeQuery = true)
    StatHits findAllByUri(LocalDateTime startTime, LocalDateTime endTime, String uri);

    @Query(value = "select count(distinct s.ip) as hits, s.app as app, s.uri as uri from stats s where " +
            "(s.timestamp between :startTime and :endTime) and (s.uri = :uri) group by s.app, s.uri",
            nativeQuery = true)
    StatHits findUniqueByUri(LocalDateTime startTime, LocalDateTime endTime, String uri);

    @Query(value = "select count(s.id) as hits, s.app as app, s.uri as uri from stats s where " +
            "(s.timestamp between :startTime and :endTime) group by s.app, s.uri",
            nativeQuery = true)
    List<StatHits> findAll(LocalDateTime startTime, LocalDateTime endTime);

    @Query(value = "select count(distinct s.ip) as hits, s.app as app, s.uri as uri from stats s where " +
            "(s.timestamp between :startTime and :endTime) group by s.app, s.uri",
            nativeQuery = true)
    List<StatHits> findUnique(LocalDateTime startTime, LocalDateTime endTime);
}
