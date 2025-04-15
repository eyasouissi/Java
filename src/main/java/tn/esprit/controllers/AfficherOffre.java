package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.entities.Offre;
import tn.esprit.services.offreService;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherOffre {

    @FXML
    private TableView<Offre> tableOffres;

    @FXML
    private TableColumn<Offre, String> colNom;

    @FXML
    private TableColumn<Offre, String> colImage;

    @FXML
    private TableColumn<Offre, Double> colPrix;

    @FXML
    private TableColumn<Offre, String> colDateDebut;

    @FXML
    private TableColumn<Offre, String> colDateFin;

    @FXML
    private TableColumn<Offre, String> colDescription;

    @FXML
    private TableColumn<Offre, Void> colAction;

    private final offreService offreService = new offreService();

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("name"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("price"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        try {
            List<Offre> offres = offreService.recuperer();
            tableOffres.getItems().setAll(offres);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des offres.");
        }

        ajouterBoutonsActions();
    }

    private void ajouterBoutonsActions() {
        colAction.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Offre, Void> call(final TableColumn<Offre, Void> param) {
                return new TableCell<>() {

                    private final Button modifierBtn = new Button("✏ Modifier");
                    private final Button deleteBtn = new Button("🗑 Supprimer");

                    {
                        // Appliquer les styles CSS définis dans style.css
                        modifierBtn.getStyleClass().add("modifier-btn");
                        deleteBtn.getStyleClass().add("supprimer-btn");

                        // Action pour supprimer une offre
                        deleteBtn.setOnAction(event -> {
                            Offre offre = getTableView().getItems().get(getIndex());
                            try {
                                offreService.supprimer(offre);
                                getTableView().getItems().remove(offre);
                                showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre supprimée !");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer l'offre.");
                            }
                        });

                        // Action pour modifier une offre
                        modifierBtn.setOnAction(event -> {
                            Offre offre = getTableView().getItems().get(getIndex());
                            openModifierOffreWindow(offre);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10, modifierBtn, deleteBtn);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }

    private void openModifierOffreWindow(Offre offre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/ModifierOffre.fxml"));
            Parent root = loader.load();

            ModifierOffre modifierOffreController = loader.getController();
            modifierOffreController.setOffre(offre);
            modifierOffreController.setTableOffres(tableOffres);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Modifier Offre");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification.");
        }
    }

    @FXML
    private void allerVersAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/offre.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            Stage stage = (Stage) tableOffres.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Ajouter une Offre");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page d'ajout !");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void allerVersPaiement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/AfficherPaiement.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            Stage stage = (Stage) tableOffres.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Liste des Paiements");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page des paiements !");
        }
    }

}
