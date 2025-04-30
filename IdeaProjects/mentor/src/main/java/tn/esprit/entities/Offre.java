package tn.esprit.entities;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Offre {
    private Long id;
    private String name = "";
    private String imagePath;
    private Double price;
    private LocalDateTime startDate;
    private LocalDate endDate;
    private String description;
    private List<Paiement> paiements = new ArrayList<>();

    public Offre() {
        this.startDate = LocalDateTime.now();
    }

    public Offre(String name, Double price, String description) {
        this();
        this.name = name;
        this.price = price;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Paiement> getPaiements() { return paiements; }
    public void setPaiements(List<Paiement> paiements) { this.paiements = paiements; }

    public void addPaiement(Paiement paiement) {
        if (!paiements.contains(paiement)) {
            paiements.add(paiement);
            paiement.setOffre(this);
        }
    }

    public void removePaiement(Paiement paiement) {
        if (paiements.remove(paiement)) {
            paiement.setOffre(null);
        }
    }

    @Override
    public String toString() {
        return "Offre{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", paiementsCount=" + paiements.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Offre offre = (Offre) o;
        return Objects.equals(id, offre.id) &&
                Objects.equals(name, offre.name) &&
                Objects.equals(startDate, offre.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startDate);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private Double price;
        private String description;
        private LocalDateTime startDate;
        private LocalDate endDate;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(Double price) {
            this.price = price;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Offre build() {
            Offre offre = new Offre(name, price, description);
            offre.setStartDate(startDate != null ? startDate : LocalDateTime.now());
            offre.setEndDate(endDate);
            return offre;
        }
    }
}