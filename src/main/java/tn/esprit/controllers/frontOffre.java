// frontOffre.java
package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.stage.Stage;
import tn.esprit.entities.Offre;
import tn.esprit.services.offreService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class frontOffre {

    @FXML
    private HBox offersContainer;

    private offreService offreService;
    @FXML
    private Button profileBtn;

    @FXML
    private void goHome() {
        // Code pour naviguer vers la page d'accueil
    }

    @FXML
    private void goAbout() {
        // Code pour naviguer vers la page 'About'
    }

    @FXML
    private void goForum() {
        // Code pour aller au forum
    }

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
        offerBox.setPrefWidth(260);
        offerBox.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 12px;
            -fx-background-radius: 12px;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.3, 0, 4);
            -fx-padding: 15px;
            -fx-alignment: center;
        """);

        // Image
        ImageView imageView = new ImageView();
        if (offre.getImagePath() != null && !offre.getImagePath().isEmpty()) {
            File imageFile = new File("src/main/resources/images/" + offre.getImagePath());
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
                imageView.setFitWidth(230);
                imageView.setFitHeight(120);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                offerBox.getChildren().add(imageView);
            }
        }

        Label nomOffre = new Label(offre.getName());
        nomOffre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4b0082;");

        Label prixOffre = new Label("$" + offre.getPrice() + " / mois");
        prixOffre.setStyle("-fx-font-size: 15px; -fx-text-fill: #6a5acd;");

        Label description = new Label("\uD83D\uDCDD " + offre.getDescription());
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 13px; -fx-text-fill: #4b0082;");

        Label dateDebut = new Label("\uD83D\uDCC5 Début : " + offre.getStartDate().toLocalDate());
        dateDebut.setStyle("-fx-font-size: 12px; -fx-text-fill: #4b0082;");

        Label dateFin = new Label("\uD83D\uDCC5 Fin : " + offre.getEndDate());
        dateFin.setStyle("-fx-font-size: 12px; -fx-text-fill: #4b0082;");

        Button btnAbonner = new Button("S'abonner");
        btnAbonner.setStyle("""
            -fx-background-color: #6a5acd;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 8px;
            -fx-padding: 8 16;
        """);
        btnAbonner.setOnAction(e -> onSubscribeButtonClick(offre));

        offerBox.getChildren().addAll(nomOffre, prixOffre, description, dateDebut, dateFin, btnAbonner);
        return offerBox;
    }

    private void onSubscribeButtonClick(Offre offre) {
        System.out.println("L'utilisateur a souscrit à l'offre : " + offre.getName());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/AjouterPaiement.fxml"));
            Parent root = loader.load();
            AjouterPaiement ajouterPaiementController = loader.getController();
            if (ajouterPaiementController != null) {
                ajouterPaiementController.setOffre(offre);
            }
            Scene currentScene = offersContainer.getScene();
            if (currentScene != null) {
                Stage stage = (Stage) currentScene.getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
