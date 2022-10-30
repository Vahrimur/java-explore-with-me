package ru.practicum.stats.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHit {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return "{\"app\": \"" + this.app + "\",\n" +
                "  \"uri\": \"" + this.uri + "\",\n" +
                "  \"ip\": \"" + this.ip + "\",\n" +
                "  \"timestamp\": \""
                + this.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\"}";
    }
}
