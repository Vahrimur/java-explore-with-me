package ru.practicum.stats.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.stats.model.EndpointHit;

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
public class StatClient {
    private final URI uri;
    private final HttpClient client = HttpClient.newHttpClient();

    @Autowired
    public StatClient(@Value("${ewm-stats.url}") String uriStr) {
        this.uri = URI.create(uriStr + "/hit");
    }

    public void sendStats(HttpServletRequest request) throws IOException {
        try {
            HttpRequest statRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "text/html")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            new EndpointHit(
                                    null,
                                    "ewm-server",
                                    request.getRequestURI(),
                                    request.getRemoteAddr(),
                                    LocalDateTime.now()
                            ).toString()))
                    .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            client.send(statRequest, handler);
        } catch (IOException | InterruptedException exception) {
            throw new IOException("Incorrect httpRequest");
        }
    }
}
