package ru.practicum.model;

import lombok.Data;

@Data
public class ViewStats {
    private Long hits;
    private String app;
    private String uri;

    public ViewStats() {
    }

    public ViewStats(Long hits, String app, String uri) {
        this.hits = hits;
        this.app = app;
        this.uri = uri;
    }
}
