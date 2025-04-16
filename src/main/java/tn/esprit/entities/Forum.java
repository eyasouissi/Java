package tn.esprit.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "forum")
public class Forum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    @NotBlank(message = "Please fill this field")
    private String title;

    @Column(length = 255, nullable = false)
    @NotBlank(message = "Please fill this field")
    @Size(max = 255, message = "Description cannot be longer than 255 characters")
    private String description;

    @Column(nullable = true)
    private Integer totalPosts = 0;

    @OneToMany(mappedBy = "forum", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Column(nullable = true)
    private Boolean isPublic = false;

    @Column(nullable = false)
    @NotNull(message = "Creation date should not be blank")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @NotNull(message = "Update date should not be blank")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Integer views = 0;

    @Column(length = 255, nullable = true)
    private String topics;

    // Constructors
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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(Integer totalPosts) {
        this.totalPosts = totalPosts;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public Boolean isPublic() {
        return isPublic;
    }

    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    // Relationship management methods
    public void addPost(Post post) {
        if (!posts.contains(post)) {
            posts.add(post);
            post.setForum(this);
            this.totalPosts = posts.size();
        }
    }

    public void removePost(Post post) {
        if (posts.remove(post)) {
            post.setForum(null);
            this.totalPosts = posts.size();
        }
    }

    // Business methods
    public void incrementViews() {
        this.views++;
    }

    // toString() method
    @Override
    public String toString() {
        return "Forum{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", totalPosts=" + totalPosts +
                ", postsCount=" + posts.size() +
                ", isPublic=" + isPublic +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", views=" + views +
                ", topics='" + topics + '\'' +
                '}';
    }

    // equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Forum forum = (Forum) o;
        return Objects.equals(id, forum.id) &&
                Objects.equals(title, forum.title) &&
                Objects.equals(createdAt, forum.createdAt);
    }

    // hashCode() method
    @Override
    public int hashCode() {
        return Objects.hash(id, title, createdAt);
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String description;
        private Boolean isPublic = false;
        private String topics;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder isPublic(Boolean isPublic) {
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