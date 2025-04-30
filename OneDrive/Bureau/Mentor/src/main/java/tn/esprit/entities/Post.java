package tn.esprit.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;

public class Post {
    private Long id;
    private Forum forum;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likes ;
    private Set<String> photos = new HashSet<>();
    private String gifUrl;
    private User user;

    // Relationships
    private List<Comment> comments = new ArrayList<>();
    private Set<User> likedByUsers = new HashSet<>();
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

    public int getLikes() {
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