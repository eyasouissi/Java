package tn.esprit.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
// import org.springframework.security.core.GrantedAuthority;  //
// import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "user")
public class User /* implements UserDetails */ {  //
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @Column(length = 255)
    private String bio;

    @Column(length = 255)
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, message = "Name should be at least 2 characters long")
    private String name;

    @Column(length = 255)
    private String gender;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_requested_at")
    private LocalDateTime passwordResetRequestedAt;

    @Column(length = 255)
    private String diploma;

    @Column(length = 255)
    private String speciality;

    private Integer age;

    @Column(length = 255)
    private String country;

    @Transient
    @NotBlank(message = "Password cannot be blank", groups = OnCreate.class)
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String plainPassword;

    @Column(name = "verification_token", length = 255)
    private String verificationToken;

    @Column(length = 255)
    private String profilePicture;

    @Column(length = 255)
    private String backgroundImage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @ManyToMany(mappedBy = "likedByUsers")
    private Set<Post> likedPosts = new HashSet<>();

    @Column(length = 5)
    private String locale = "en";

    @ManyToMany(mappedBy = "members")
    private Set<GroupStudent> groups = new HashSet<>();

    @Column(name = "karma_points")
    private Integer karmaPoints = 0;

    @ManyToMany
    @JoinTable(
            name = "user_completed_levels",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "level_id")
    )
    private Set<Level> completedLevels = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @Column(name = "oauth_id", length = 255)
    private String oauthId;

    @Column(name = "oauth_type", length = 255)
    private String oauthType;

    @Column(name = "is_restricted")
    private Boolean isRestricted = false;

    @Column(name = "google_id", length = 255)
    private String googleId;

    // Constructors
    public User() {
        this.creationDate = LocalDateTime.now();
        this.roles.add("ROLE_USER");
    }

    public User(String email, String name, String password) {
        this();
        this.email = email;
        this.name = name;
        this.password = password;
    }

    /*
    // =========== SECURITY METHODS (COMMENTED OUT) ===========
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> (GrantedAuthority) () -> role)
                .toList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isRestricted;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isVerified;
    }
    */

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public LocalDateTime getPasswordResetRequestedAt() {
        return passwordResetRequestedAt;
    }

    public void setPasswordResetRequestedAt(LocalDateTime passwordResetRequestedAt) {
        this.passwordResetRequestedAt = passwordResetRequestedAt;
    }

    public String getDiploma() {
        return diploma;
    }

    public void setDiploma(String diploma) {
        this.diploma = diploma;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public Set<Post> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(Set<Post> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Set<GroupStudent> getGroups() {
        return groups;
    }

    public void setGroups(Set<GroupStudent> groups) {
        this.groups = groups;
    }

    public Integer getKarmaPoints() {
        return karmaPoints;
    }

    public void setKarmaPoints(Integer karmaPoints) {
        this.karmaPoints = karmaPoints;
    }

    public Set<Level> getCompletedLevels() {
        return completedLevels;
    }

    public void setCompletedLevels(Set<Level> completedLevels) {
        this.completedLevels = completedLevels;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public String getOauthId() {
        return oauthId;
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;
    }

    public String getOauthType() {
        return oauthType;
    }

    public void setOauthType(String oauthType) {
        this.oauthType = oauthType;
    }

    public Boolean getRestricted() {
        return isRestricted;
    }

    public void setRestricted(Boolean restricted) {
        isRestricted = restricted;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    // Business methods
    public void addPost(Post post) {
        posts.add(post);
        post.setUser(this);
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setUser(null);
    }

    public void likePost(Post post) {
        likedPosts.add(post);
        post.getLikedByUsers().add(this);
    }

    public void unlikePost(Post post) {
        likedPosts.remove(post);
        post.getLikedByUsers().remove(this);
    }

    public void addGroup(GroupStudent group) {
        groups.add(group);
        group.getMembers().add(this);
    }

    public void removeGroup(GroupStudent group) {
        groups.remove(group);
        group.getMembers().remove(this);
    }

    public void completeLevel(Level level) {
        if (!completedLevels.contains(level)) {
            completedLevels.add(level);
            karmaPoints++;
        }
    }

    public boolean hasCompletedLevel(Level level) {
        return completedLevels.contains(level);
    }

    public void addRating(Rating rating) {
        ratings.add(rating);
        rating.setUser(this);
    }

    public void removeRating(Rating rating) {
        ratings.remove(rating);
        rating.setUser(null);
    }

    // toString()
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", roles=" + roles +
                ", isVerified=" + isVerified +
                ", karmaPoints=" + karmaPoints +
                '}';
    }

    // equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email;
        private String name;
        private String password;
        private Set<String> roles = new HashSet<>();

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder role(String role) {
            this.roles.add(role);
            return this;
        }

        public User build() {
            User user = new User(email, name, password);
            user.setRoles(roles);
            return user;
        }
    }

    // Validation group interface
    public interface OnCreate {}
}