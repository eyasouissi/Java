package tn.esprit.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;  // Changed to Integer to match int(11)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", referencedColumnName = "id")
    private Forum forum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "content", length = 2000, nullable = false)
    @NotBlank(message = "Content cannot be empty.")
    @Size(max = 2000, message = "Content cannot exceed 2000 characters.")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "likes", nullable = false)
    private Integer likes = 0;

    @Column(name = "photos", columnDefinition = "LONGTEXT")
    private String photos;  // Changed to String to match longtext

    @Column(name = "gif_url", length = 255)
    @URL(message = "Please enter a valid URL")
    @Pattern(regexp = "^(https?://)(.*\\.(gif))$", message = "URL must point to a GIF file")
    private String gifUrl;

    // Removed ElementCollection as we're using single text column
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // Removed notifications as not present in DB schema
    // Keep only fields that exist in the table

    public Post() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Updated constructor
    public Post(String content, User user, Forum forum) {
        this();
        this.content = content;
        this.user = user;
        this.forum = forum;
    }

    // Getters/Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Forum getForum() { return forum; }
    public void setForum(Forum forum) { this.forum = forum; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }

    public String getPhotos() { return photos; }
    public void setPhotos(String photos) { this.photos = photos; }

    public String getGifUrl() { return gifUrl; }
    public void setGifUrl(String gifUrl) { this.gifUrl = gifUrl; }

    // Removed methods for notifications and likedByUsers as they're not in DB schema

    // toString() with database alignment
    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", forum=" + (forum != null ? forum.getId() : null) +
                ", user=" + (user != null ? user.getId() : null) +
                ", content='" + content.substring(0, Math.min(30, content.length())) + "..." + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", likes=" + likes +
                ", gifUrl='" + gifUrl + '\'' +
                '}';
    }
}