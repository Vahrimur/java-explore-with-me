package ru.practicum.event.model;

import lombok.Data;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation", nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime created;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @OneToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @Column(name = "location_lat", nullable = false)
    private Float lat;
    @Column(name = "location_lon", nullable = false)
    private Float lon;
    @Column(name = "paid", nullable = false)
    private Boolean paid;
    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;
    @Column(name = "published_on", nullable = false)
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private EventState state;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "views")
    private Integer views;

    public Event() {
    }

    public Event(Long id,
                 String annotation,
                 Category category,
                 Integer confirmedRequests,
                 LocalDateTime created,
                 String description,
                 LocalDateTime eventDate,
                 User initiator,
                 Float lat,
                 Float lon,
                 Boolean paid,
                 Integer participantLimit,
                 LocalDateTime publishedOn,
                 Boolean requestModeration,
                 EventState state,
                 String title,
                 Integer views) {
        this.id = id;
        this.annotation = annotation;
        this.category = category;
        this.confirmedRequests = confirmedRequests;
        this.created = created;
        this.description = description;
        this.eventDate = eventDate;
        this.initiator = initiator;
        this.lat = lat;
        this.lon = lon;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
        this.title = title;
        this.views = views;
    }
}
