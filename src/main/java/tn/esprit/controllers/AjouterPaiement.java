package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import tn.esprit.entities.Offre;
import tn.esprit.entities.Paiement;
import tn.esprit.entities.User;
import tn.esprit.services.paiementService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AjouterPaiement {

    @FXML
    private ComboBox<User> userComboBox;

    @FXML
    private DatePicker datePaiementPicker;

    @FXML
    private Label statusLabel;

    @FXML
    private Label prixLabel;

    @FXML
    private Label offreLabel;
    @FXML
    private void goHome() {
        // Navigation vers accueil
    }

    @FXML
    private void goOffres() {
        // Navigation vers la page d'offres
    }

    private final paiementService paiementService = new paiementService();
    private Offre offre;

    // Appelée depuis un autre contrôleur
    public void setOffre(Offre offre) {
        this.offre = offre;
        if (offreLabel != null && prixLabel != null && offre != null) {
            offreLabel.setText("Offre : " + offre.getName());
            prixLabel.setText("Prix : " + offre.getPrice() + "$");
        }
    }

    @FXML
    public void initialize() {
        try {
            ObservableList<User> users = FXCollections.observableArrayList(getUsers());
            userComboBox.setItems(users);

            // N'affiche que le nom de l'utilisateur dans la ComboBox
            userComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(User user) {
                    return user != null ? user.getName() : "";
                }

                @Override
                public User fromString(String string) {
                    return null; // Non utilisé ici
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors du chargement des utilisateurs : " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void ajouterPaiement() {
        User selectedUser = userComboBox.getValue();
        LocalDate selectedDate = datePaiementPicker.getValue();

        if (selectedUser == null || selectedDate == null || offre == null) {
            statusLabel.setText("Veuillez sélectionner un utilisateur, une offre, et une date.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        LocalDateTime paymentDate = selectedDate.atStartOfDay();
        Paiement paiement = new Paiement(selectedUser, offre, paymentDate);

        try {
            paiementService.ajouter(paiement);
            statusLabel.setText("Paiement ajouté avec succès !");
            statusLabel.setStyle("-fx-text-fill: green;");
        } catch (SQLException e) {
            e.printStackTrace(); // Affiche l'erreur SQL dans la console
            statusLabel.setText("Erreur lors de l'ajout du paiement : " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private List<User> getUsers() throws SQLException {  // Ajout de l'exception SQLException
        return paiementService.getUsers(); // Appeler le service pour récupérer les utilisateurs depuis la base de données
    }
}
