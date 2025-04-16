package tn.esprit.entities;

import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class Category {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final BooleanProperty isActive = new SimpleBooleanProperty();
    private final StringProperty icon = new SimpleStringProperty();

    // Constructeur principal
    public Category(String name, String description, LocalDateTime createdAt, boolean isActive, String icon) {
        this.name.set(name);
        this.description.set(description);
        this.createdAt.set(createdAt);
        this.isActive.set(isActive);
        this.icon.set(icon);
    }

    // Constructeur complet avec id
    public Category(int id, String name, String description, LocalDateTime createdAt, boolean isActive, String icon) {
        this.id.set(id);
        this.name.set(name);
        this.description.set(description);
        this.createdAt.set(createdAt);
        this.isActive.set(isActive);
        this.icon.set(icon);
    }

    public Category() {

    }

    // Getters
    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getDescription() {
        return description.get();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public boolean getIsActive() {
        return isActive.get();
    }

    public String getIcon() {
        return icon.get();
    }

    // Setters
    public void setId(int id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    public void setIsActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    public void setIcon(String icon) {
        this.icon.set(icon);
    }

    // Property getters pour JavaFX Binding
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    public StringProperty iconProperty() {
        return icon;
    }

    // equals() et hashCode() basés sur id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return Objects.equals(id.get(), category.id.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.get());
    }

    // toString()
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id.get() +
                ", name='" + name.get() + '\'' +
                ", description='" + description.get() + '\'' +
                ", createdAt=" + createdAt.get() +
                ", isActive=" + isActive.get() +
                ", icon='" + icon.get() + '\'' +
                '}';
    }
}
