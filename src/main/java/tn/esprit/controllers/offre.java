package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Offre;
import tn.esprit.services.offreService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class offre {

    @FXML
    private TextField nom_offre, image_offre, prix, date_debut, date_fin, description;

    @FXML
    private Button btnAjouter, btnChoisirImage;

    private final offreService offreService = new offreService();

    @FXML
    void initialize() {
        date_debut.setText(LocalDate.now().toString());
        btnAjouter.setOnAction(this::ajouterOffre);
        btnChoisirImage.setOnAction(this::choisirImageDepuisPC);
    }

    private void choisirImageDepuisPC(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                // Copier le fichier dans src/main/resources/images/
                File destDir = new File("src/main/resources/images/");
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, selectedFile.getName());
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour le champ image avec le nom du fichier
                image_offre.setText(selectedFile.getName());

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la copie de l'image : " + e.getMessage());
            }
        }
    }

    private void ajouterOffre(ActionEvent event) {
        try {
            String nom = nom_offre.getText();
            if (nom.matches("\\d+")) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom de l'offre ne peut pas être un nombre.");
                return;
            }

            String image = image_offre.getText();
            double prixValue = Double.parseDouble(prix.getText());

            LocalDate dateDebut = LocalDate.parse(date_debut.getText());
            LocalDate dateFin = LocalDate.parse(date_fin.getText());

            if (!dateFin.isAfter(dateDebut)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La date de fin doit être après la date de début.");
                return;
            }

            String desc = description.getText();

            Offre offre = new Offre(nom, image, prixValue, dateDebut.atStartOfDay(), dateFin, desc);
            offreService.ajouter(offre);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre ajoutée avec succès !");
            clearFields();
            date_debut.setText(LocalDate.now().toString());

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le prix doit être un nombre.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    private void clearFields() {
        nom_offre.clear();
        image_offre.clear();
        prix.clear();
        date_debut.clear();
        date_fin.clear();
        description.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void allerVersAffichage(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/interfaces/AfficherOffre.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.scene.Scene scene = ((Button) event.getSource()).getScene();
            javafx.stage.Stage stage = (javafx.stage.Stage) scene.getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Liste des Offres");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page d'affichage !");
        }
    }
}
