package ru.practicum.compilation.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "compilations", schema = "public")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "pinned")
    private Boolean pinned;

    public Compilation() {
    }

    public Compilation(Long id, String title, Boolean pinned) {
        this.id = id;
        this.title = title;
        this.pinned = pinned;
    }
}
