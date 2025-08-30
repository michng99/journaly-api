package com.journaly.api.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // THÊM GETTER VÀ SETTER CHO USER
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "sentiment_label", length = 50)
    private String sentimentLabel;

    @Column(name = "positive_score", precision = 5, scale = 4)
    private BigDecimal positiveScore;

    @Column(name = "negative_score", precision = 5, scale = 4)
    private BigDecimal negativeScore;

    @Column(name = "neutral_score", precision = 5, scale = 4)
    private BigDecimal neutralScore;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "entry_tags",
            joinColumns = @JoinColumn(name = "entry_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // --- GETTERS AND SETTERS ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSentimentLabel() {
        return sentimentLabel;
    }

    public void setSentimentLabel(String sentimentLabel) {
        this.sentimentLabel = sentimentLabel;
    }

    // LỖI NẰM Ở ĐÂY: Kiểu dữ liệu phải là BigDecimal
    public BigDecimal getPositiveScore() {
        return positiveScore;
    }

    public void setPositiveScore(BigDecimal positiveScore) {
        this.positiveScore = positiveScore;
    }

    public BigDecimal getNegativeScore() {
        return negativeScore;
    }

    public void setNegativeScore(BigDecimal negativeScore) {
        this.negativeScore = negativeScore;
    }

    public BigDecimal getNeutralScore() {
        return neutralScore;
    }

    public void setNeutralScore(BigDecimal neutralScore) {
        this.neutralScore = neutralScore;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}