package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.StatHits;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatsRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public void createHit(EndpointHit endpointHit) {
        statsRepository.save(endpointHit);
    }

    @Override
    public List<ViewStats> getStatsByParams(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(
                URLDecoder.decode(start, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
        LocalDateTime endTime = LocalDateTime.parse(
                URLDecoder.decode(end, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        List<ViewStats> viewStats = new ArrayList<>();

        if (uris == null) {
            List<StatHits> hits;
            if (unique) {
                hits = statsRepository.findUnique(startTime, endTime);
            } else {
                hits = statsRepository.findAll(startTime, endTime);
            }
            for (StatHits hit : hits) {
                ViewStats newViewStat = new ViewStats(hit.getHits(), hit.getApp(), hit.getUri());
                viewStats.add(newViewStat);
            }
            return viewStats;
        }

        for (String uri : uris) {
            StatHits hits;
            if (unique) {
                hits = statsRepository.findUniqueByUri(startTime, endTime, uri);
            } else {
                hits = statsRepository.findAllByUri(startTime, endTime, uri);
            }
            ViewStats newViewStat;
            if (hits != null) {
                newViewStat = new ViewStats(hits.getHits(), hits.getApp(), hits.getUri());
            } else {
                newViewStat = new ViewStats(0L, "explore", uri);
            }
            viewStats.add(newViewStat);
        }
        return viewStats;
    }
}
