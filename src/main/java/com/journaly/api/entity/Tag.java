package com.journaly.api.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "user_id")
    private UUID userId;

    @ManyToMany(mappedBy = "tags")
    private Set<JournalEntry> entries = new HashSet<>();

    // --- GETTERS AND SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    // PHƯƠNG THỨC BỊ THIẾU NẰM Ở ĐÂY
    public void setName(String name) {
        this.name = name;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Set<JournalEntry> getEntries() {
        return entries;
    }

    public void setEntries(Set<JournalEntry> entries) {
        this.entries = entries;
    }
}