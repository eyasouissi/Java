package tn.esprit.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Evenement {
    private Integer id;
    private String titreE;
    private String descriptionE;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String imageE;
    private Annonce annonce;

    public Evenement() {}

    public Evenement(String titreE, String descriptionE, LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.titreE = titreE;
        this.descriptionE = descriptionE;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitreE() { return titreE; }
    public void setTitreE(String titreE) { this.titreE = titreE; }
    public String getDescriptionE() { return descriptionE; }
    public void setDescriptionE(String descriptionE) { this.descriptionE = descriptionE; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public String getImageE() { return imageE; }
    public void setImageE(String imageE) { this.imageE = imageE; }
    public Annonce getAnnonce() { return annonce; }
    public void setAnnonce(Annonce annonce) { this.annonce = annonce; }

    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + id +
                ", titreE='" + titreE + '\'' +
                ", dateDebut=" + dateDebut +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evenement evenement = (Evenement) o;
        return Objects.equals(id, evenement.id) &&
                Objects.equals(titreE, evenement.titreE);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titreE);
    }
}