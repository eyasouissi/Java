package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;
import tn.esprit.entities.Paiement;
import tn.esprit.services.paiementService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherPaiement {

    @FXML private TableView<Paiement> tablePaiements;
    @FXML private TableColumn<Paiement, String> colOffre;
    @FXML private TableColumn<Paiement, String> colUtilisateur;
    @FXML private TableColumn<Paiement, String> colDate;
    @FXML private TableColumn<Paiement, Void> colAction;
    @FXML private Label lblNomOffre;
    @FXML private Label lblNomUtilisateur;

    private final paiementService paiementService = new paiementService();

    @FXML
    public void initialize() {
        // ✅ Remplissage des colonnes
        colOffre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOffre().getName())); // Affiche le nom de l'offre

        colUtilisateur.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUser().getName())); // Affiche le nom de l'utilisateur

        colDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPaymentDate().toLocalDate().toString())); // Affiche la date du paiement

        // ✅ Charger les paiements
        try {
            // Appel de la méthode recuperer pour obtenir la liste des paiements
            List<Paiement> paiements = paiementService.recuperer();

            // Ajouter les paiements récupérés à la TableView
            tablePaiements.getItems().setAll(paiements);

            // ✅ Sélection d'une ligne
            tablePaiements.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                if (newSel != null) {
                    lblNomOffre.setText("Offre: " + newSel.getOffre().getName());  // Affiche le nom de l'offre
                    lblNomUtilisateur.setText("Utilisateur: " + newSel.getUser().getName());  // Affiche le nom de l'utilisateur
                } else {
                    lblNomOffre.setText("Offre: -");
                    lblNomUtilisateur.setText("Utilisateur: -");
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des paiements.");
        }

        ajouterBoutonsActions();
    }


    private void ajouterBoutonsActions() {
        colAction.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Paiement, Void> call(final TableColumn<Paiement, Void> param) {
                return new TableCell<>() {
                    private final Button deleteBtn = new Button("🗑 Supprimer");

                    {
                        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 13px;");
                        deleteBtn.setOnAction(event -> {
                            Paiement paiement = getTableView().getItems().get(getIndex());
                            try {
                                paiementService.supprimer(paiement);
                                getTableView().getItems().remove(paiement);
                                showAlert(Alert.AlertType.INFORMATION, "Succès", "Paiement supprimé !");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le paiement.");
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new HBox(deleteBtn));
                        }
                    }
                };
            }
        });
    }

    @FXML
    private void allerVersOffre() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/AfficherOffre.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            Stage stage = (Stage) tablePaiements.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Liste des Offres");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page des offres !");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
