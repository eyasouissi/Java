package tn.esprit.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reply {
    private Long id;
    private String content;
    private User user;
    private Comment comment;
    private LocalDateTime createdAt;

    // Constructors
    public Reply() {
        this.createdAt = LocalDateTime.now();
    }

    public Reply(String content, User user, Comment comment) {
        this();
        this.content = content;
        this.user = user;
        this.comment = comment;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // toString() method
    @Override
    public String toString() {
        return "Reply{" +
                "id=" + id +
                ", content='" + (content != null ? content.substring(0, Math.min(content.length(), 30)) + "..." : "null") + '\'' +
                ", userId=" + (user != null ? user.getId() : null) +
                ", commentId=" + (comment != null ? comment.getId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }

    // equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reply reply = (Reply) o;
        return Objects.equals(id, reply.id) &&
                Objects.equals(content, reply.content) &&
                Objects.equals(createdAt, reply.createdAt);
    }

    // hashCode() method
    @Override
    public int hashCode() {
        return Objects.hash(id, content, createdAt);
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String content;
        private User user;
        private Comment comment;

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder comment(Comment comment) {
            this.comment = comment;
            return this;
        }

        public Reply build() {
            return new Reply(content, user, comment);
        }
    }
}