package tn.esprit.entities;

import java.util.Objects;

public class File {
    private Long id;
    private String fileName;
    private boolean isViewed = false; // Using primitive boolean
    private Level level;

    public File() {}

    public File(String fileName) {
        this.fileName = fileName;
    }

    public File(String fileName, Level level) {
        this(fileName);
        this.level = level;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    // Changed return type to primitive boolean
    public boolean isViewed() { return isViewed; }

    public void setViewed(boolean viewed) { this.isViewed = viewed; } // Changed to accept primitive boolean

    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }

    public String getFilePath() {
        return "/uploads/images/" + this.fileName;
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Objects.equals(id, file.id) &&
                Objects.equals(fileName, file.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String fileName;
        private Level level;
        private boolean isViewed = false; // Using primitive boolean

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder level(Level level) {
            this.level = level;
            return this;
        }

        public Builder isViewed(boolean isViewed) {
            this.isViewed = isViewed;
            return this;
        }

        public File build() {
            File file = new File(fileName, level);
            file.setViewed(isViewed);
            return file;
        }
    }
}
