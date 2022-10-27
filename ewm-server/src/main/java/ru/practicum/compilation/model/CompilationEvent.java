package ru.practicum.compilation.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "compilations_events", schema = "public")
public class CompilationEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "compilation_id")
    private Long compilationId;
    @Column(name = "event_id")
    private Long eventId;

    public CompilationEvent() {
    }

    public CompilationEvent(Integer id, Long compilationId, Long eventId) {
        this.id = id;
        this.compilationId = compilationId;
        this.eventId = eventId;
    }
}
