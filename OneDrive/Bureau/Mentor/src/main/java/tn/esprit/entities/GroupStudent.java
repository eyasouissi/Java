package tn.esprit.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;

public class GroupStudent {
    private Long id;
    private int memberCount = 0;
    private String description;
    private String name;
    private Set<User> members = new HashSet<>();
    private String pdfFile;
    private LocalDate creationDate;
    private String image;
    private LocalDate meetingDate;
    private List<Project> projects = new ArrayList<>();
    private User createdBy;

    public GroupStudent() {
        this.creationDate = LocalDate.now();
    }

    public GroupStudent(String name, String description, User createdBy) {
        this();
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<User> getMembers() { return members; }
    public void setMembers(Set<User> members) {
        this.members = members;
        this.memberCount = members.size();
    }
    public String getPdfFile() { return pdfFile; }
    public void setPdfFile(String pdfFile) { this.pdfFile = pdfFile; }
    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public LocalDate getMeetingDate() { return meetingDate; }
    public void setMeetingDate(LocalDate meetingDate) { this.meetingDate = meetingDate; }
    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public void addMember(User member) {
        if (!members.contains(member)) {
            members.add(member);
            memberCount = members.size();
        }
    }

    public void removeMember(User member) {
        if (members.remove(member)) {
            memberCount = members.size();
        }
    }

    public void addProject(Project project) {
        if (!projects.contains(project)) {
            projects.add(project);
            project.setGroup(this);
        }
    }

    public void removeProject(Project project) {
        if (projects.remove(project)) {
            project.setGroup(null);
        }
    }

    @Override
    public String toString() {
        return "GroupStudent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", membersCount=" + memberCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupStudent that = (GroupStudent) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String description;
        private User createdBy;
        private LocalDate meetingDate;
        private String image;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdBy(User createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder meetingDate(LocalDate meetingDate) {
            this.meetingDate = meetingDate;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public GroupStudent build() {
            GroupStudent group = new GroupStudent(name, description, createdBy);
            group.setMeetingDate(meetingDate);
            group.setImage(image);
            return group;
        }
    }
}
