package tn.esprit.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Forum {
    private Long id;
    private String title;
    private String description;
    private int totalPosts = 0;
    private List<Post> posts = new ArrayList<>();
    private boolean isPublic = false; // using primitive boolean
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int views = 0;
    private String topics;

    public Forum() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Forum(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public int getTotalPosts() { return totalPosts; }
    public void setTotalPosts(int totalPosts) { this.totalPosts = totalPosts; }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) {
        this.posts = posts;
        this.totalPosts = posts.size(); // also update totalPosts when setting posts
    }

    // Return type changed to primitive boolean
    public boolean isPublic() { return isPublic; }

    // Setter remains the same
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public String getTopics() { return topics; }
    public void setTopics(String topics) { this.topics = topics; }

    public void addPost(Post post) {
        if (!posts.contains(post)) {
            posts.add(post);
            totalPosts = posts.size();
        }
    }

    public void removePost(Post post) {
        if (posts.remove(post)) {
            totalPosts = posts.size();
        }
    }

    public void incrementViews() {
        this.views++;
    }

    @Override
    public String toString() {
        return "Forum{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", totalPosts=" + totalPosts +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Forum forum = (Forum) o;
        return Objects.equals(id, forum.id) &&
                Objects.equals(title, forum.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String description;
        private boolean isPublic = false; // using primitive boolean
        private String topics;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder topics(String topics) {
            this.topics = topics;
            return this;
        }

        public Forum build() {
            Forum forum = new Forum(title, description);
            forum.setPublic(isPublic);
            forum.setTopics(topics);
            return forum;
        }
    }
}
