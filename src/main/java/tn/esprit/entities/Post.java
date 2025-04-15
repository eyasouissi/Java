package tn.esprit.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Forum forum;

    @Column(length = 2000, nullable = false)
    @NotBlank(message = "Content cannot be empty.")
    @Size(max = 2000, message = "Content cannot exceed 2000 characters.")
    @Pattern(regexp = "^(?i)(?!.*\\b(admin|root|sudo)\\b).*$",
            message = "Content contains prohibited terms")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Column(nullable = false)
    private Integer likes = 0;

    @ElementCollection
    @CollectionTable(name = "post_photos", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "photo_url")
    @Size(max = 10, message = "You can upload up to 10 photos maximum.")
    private Set<String> photos = new HashSet<>();

    @Column(length = 255, nullable = true)
    @URL(message = "Please enter a valid URL")
    @Pattern(regexp = "^(https?://)(.*\\.(gif))$", message = "URL must point to a GIF file")
    private String gifUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "post_likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedByUsers = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Notif> notifications = new ArrayList<>();

    // Constructors
    public Post() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Post(String content, User user) {
        this();
        this.content = content;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
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

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Set<String> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<String> photos) {
        this.photos = photos;
    }

    public String getGifUrl() {
        return gifUrl;
    }

    public void setGifUrl(String gifUrl) {
        this.gifUrl = gifUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Relationship management methods
    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        if (!comments.contains(comment)) {
            comments.add(comment);
            comment.setPost(this);
        }
    }

    public void removeComment(Comment comment) {
        if (comments.remove(comment)) {
            comment.setPost(null);
        }
    }

    public Set<User> getLikedByUsers() {
        return likedByUsers;
    }

    public void addLikedByUser(User user) {
        likedByUsers.add(user);
    }

    public void removeLikedByUser(User user) {
        likedByUsers.remove(user);
    }

    public boolean isLikedByUser(User user) {
        return likedByUsers.contains(user);
    }

    public List<Notif> getNotifications() {
        return notifications;
    }

    public void addNotification(Notif notification) {
        if (!notifications.contains(notification)) {
            notifications.add(notification);
            notification.setPost(this);
        }
    }

    public void removeNotification(Notif notification) {
        if (notifications.remove(notification)) {
            notification.setPost(null);
        }
    }

    // Business methods
    public void incrementLikes() {
        this.likes++;
    }

    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
    }

    // toString() method
    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", forumId=" + (forum != null ? forum.getId() : null) +
                ", content='" + (content != null ? content.substring(0, Math.min(content.length(), 30)) + "..." : "null") + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", commentsCount=" + comments.size() +
                ", likes=" + likes +
                ", photosCount=" + photos.size() +
                ", gifUrl='" + gifUrl + '\'' +
                ", userId=" + (user != null ? user.getId() : null) +
                ", likedByUsersCount=" + likedByUsers.size() +
                ", notificationsCount=" + notifications.size() +
                '}';
    }

    // equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id) &&
                Objects.equals(content, post.content) &&
                Objects.equals(createdAt, post.createdAt);
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
        private Forum forum;
        private Set<String> photos = new HashSet<>();
        private String gifUrl;

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder forum(Forum forum) {
            this.forum = forum;
            return this;
        }

        public Builder photos(Set<String> photos) {
            this.photos = photos;
            return this;
        }

        public Builder gifUrl(String gifUrl) {
            this.gifUrl = gifUrl;
            return this;
        }

        public Post build() {
            Post post = new Post(content, user);
            post.setForum(forum);
            post.setPhotos(photos);
            post.setGifUrl(gifUrl);
            return post;
        }
    }
}