package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.stage.Stage;
import tn.esprit.entities.Offre;
import tn.esprit.services.offreService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class frontOffre {

    @FXML
    private HBox offersContainer;

    private offreService offreService;

    public frontOffre() {
        offreService = new offreService();
    }

    public void initialize() {
        try {
            List<Offre> offres = offreService.recuperer();
            for (Offre offre : offres) {
                VBox offerBox = createOfferBox(offre);
                offersContainer.getChildren().add(offerBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createOfferBox(Offre offre) {
        VBox offerBox = new VBox(10);
        offerBox.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 10px;
            -fx-background-radius: 10px;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 4);
            -fx-padding: 20px;
            -fx-cursor: hand;
            -fx-alignment: center;
            -fx-pref-width: 250px;
        """);

        offerBox.setOnMouseEntered(e -> offerBox.setStyle(offerBox.getStyle() +
                "-fx-scale-x: 1.02; -fx-scale-y: 1.02;"));
        offerBox.setOnMouseExited(e -> offerBox.setStyle(offerBox.getStyle()
                .replaceAll("-fx-scale-x: 1.02;", "")
                .replaceAll("-fx-scale-y: 1.02;", "")));

        Label nomOffre = new Label(offre.getName());
        nomOffre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #4b0082;");

        Label prixOffre = new Label("$" + offre.getPrice() + " / mois");
        prixOffre.setStyle("-fx-font-size: 16px; -fx-text-fill: #6a5acd;");

        Label descriptionOffre = new Label(offre.getDescription());
        descriptionOffre.setWrapText(true);
        descriptionOffre.setStyle("-fx-font-size: 14px; -fx-text-fill: #4b0082;");

        Label dateDebutOffre = new Label("Date début : " + offre.getStartDate().toLocalDate());
        dateDebutOffre.setStyle("-fx-font-size: 14px; -fx-text-fill: #4b0082;");

        Label dateFinOffre = new Label("Date fin : " + offre.getEndDate());
        dateFinOffre.setStyle("-fx-font-size: 14px; -fx-text-fill: #4b0082;");

        Button subscribeButton = new Button("S'abonner");
        subscribeButton.setStyle("""
            -fx-background-color: #6a5acd;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 10px;
            -fx-padding: 10 20;
        """);
        subscribeButton.setOnAction(event -> onSubscribeButtonClick(offre));

        offerBox.getChildren().addAll(nomOffre, prixOffre, descriptionOffre, dateDebutOffre, dateFinOffre, subscribeButton);
        return offerBox;
    }

    private void onSubscribeButtonClick(Offre offre) {
        System.out.println("L'utilisateur a souscrit à l'offre : " + offre.getName());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/AjouterPaiement.fxml"));
            Parent root = loader.load();

            // Vérification du contrôleur
            AjouterPaiement ajouterPaiementController = loader.getController();
            if (ajouterPaiementController != null) {
                ajouterPaiementController.setOffre(offre);
            }

            // Affichage de la nouvelle scène
            Scene currentScene = offersContainer.getScene();
            if (currentScene != null) {
                Stage stage = (Stage) currentScene.getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } else {
                System.out.println("Erreur : La scène actuelle est null.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
