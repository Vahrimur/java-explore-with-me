/*package ru.practicum.stats.service;

import io.micrometer.core.instrument.Measurement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.model.EndpointHit;
import ru.practicum.stats.config.ConfigProperties;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService{
    private ConfigProperties configProperties;
    private final URI uri = URI.create(configProperties.getStatistic() + "/hit");
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public void sendStats(HttpServletRequest request) {
        try {
            HttpRequest statRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "text/html")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            new EndpointHit(
                                    null,
                                    "explore",
                                    request.getRequestURI(),
                                    request.getRemoteAddr(),
                                    LocalDateTime.now()
                            ).toString()))
                    .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            client.send(statRequest, handler);
        } catch (IOException | InterruptedException exception) {
            log.error("Stats service error.");
        }
    }
}*/
