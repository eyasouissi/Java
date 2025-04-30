package tn.esprit.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Annonce {
    private Integer id;
    private String imageA;
    private String titreA;
    private String descriptionA;
    private LocalDateTime dateA;
    private Evenement evenement;

    public Annonce() {
        this.dateA = LocalDateTime.now();
    }

    public Annonce(String titreA, String descriptionA) {
        this();
        this.titreA = titreA;
        this.descriptionA = descriptionA;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getImageA() { return imageA; }
    public void setImageA(String imageA) { this.imageA = imageA; }
    public String getTitreA() { return titreA; }
    public void setTitreA(String titreA) { this.titreA = titreA; }
    public String getDescriptionA() { return descriptionA; }
    public void setDescriptionA(String descriptionA) { this.descriptionA = descriptionA; }
    public LocalDateTime getDateA() { return dateA; }
    public void setDateA(LocalDateTime dateA) { this.dateA = dateA; }
    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }

    @Override
    public String toString() {
        return "Annonce{" +
                "id=" + id +
                ", titreA='" + titreA + '\'' +
                ", dateA=" + dateA +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Annonce annonce = (Annonce) o;
        return Objects.equals(id, annonce.id) &&
                Objects.equals(titreA, annonce.titreA);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titreA);
    }
}