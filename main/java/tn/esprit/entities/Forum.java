package tn.esprit.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import javafx.beans.property.*;
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

    @Column(name = "title", nullable = false, length = 255)
    @NotBlank(message = "Title is required")
    private String title;

    @Column(name = "description", nullable = false, length = 2000)  // Increased from 255
    @NotBlank(message = "Description is required")
    private String description;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "topics", length = 255)
    private String topics;

    @Column(name = "views", nullable = false)
    private Integer views = 0;

    @Column(name = "totalposts")
    private Integer totalPosts = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "forum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Transient
    private final BooleanProperty isPublicProperty = new SimpleBooleanProperty();

    public Forum() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        syncProperties();
    }

    public Forum(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    private void syncProperties() {
        isPublicProperty.set(isPublic);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
        syncProperties();
    }

    public String getTopics() { return topics; }
    public void setTopics(String topics) { this.topics = topics; }

    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }

    public Integer getTotalPosts() { return totalPosts; }
    public void setTotalPosts(Integer totalPosts) { this.totalPosts = totalPosts; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }

    // JavaFX Property Accessors
    public BooleanProperty isPublicProperty() { return isPublicProperty; }

    // Business Methods
    public void incrementViews() { this.views++; }

    public void addPost(Post post) {
        posts.add(post);
        post.setForum(this);
        totalPosts = posts.size();
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setForum(null);
        totalPosts = posts.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Forum forum = (Forum) o;
        return Objects.equals(id, forum.id) &&
                Objects.equals(title, forum.title) &&
                Objects.equals(createdAt, forum.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, createdAt);
    }

    @Override
    public String toString() {
        return "Forum{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isPublic=" + isPublic +
                ", topics='" + topics + '\'' +
                ", views=" + views +
                ", totalPosts=" + totalPosts +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}