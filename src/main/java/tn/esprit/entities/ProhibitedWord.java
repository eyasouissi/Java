package tn.esprit.entities;

import java.time.Instant;
import java.util.Objects;

public class ProhibitedWord {
    public enum Category {
        PROFANITY,
        HATE_SPEECH,
        PERSONAL_INFO,
        OTHER
    }

    private Long id;
    private String word;
    private Category category;
    private int severity = 1;
    private Instant createdAt;

    // Constructors
    public ProhibitedWord() {
        this.createdAt = Instant.now();
    }

    public ProhibitedWord(String word, Category category) {
        this();
        this.word = word;
        this.category = category;
    }

    public ProhibitedWord(String word, Category category, int severity) {
        this(word, category);
        this.severity = severity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // toString() method
    @Override
    public String toString() {
        return "ProhibitedWord{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", category=" + category +
                ", severity=" + severity +
                ", createdAt=" + createdAt +
                '}';
    }

    // equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProhibitedWord that = (ProhibitedWord) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(word, that.word);
    }

    // hashCode() method
    @Override
    public int hashCode() {
        return Objects.hash(id, word);
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String word;
        private Category category;
        private int severity = 1;

        public Builder word(String word) {
            this.word = word;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder severity(int severity) {
            this.severity = severity;
            return this;
        }

        public ProhibitedWord build() {
            return new ProhibitedWord(word, category, severity);
        }
    }
}
