package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import tn.esprit.entities.Paiement;
import tn.esprit.services.paiementService;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class AfficherPaiement {

    @FXML
    private TableView<Paiement> tablePaiements;

    @FXML
    private TableColumn<Paiement, String> colUser;

    @FXML
    private TableColumn<Paiement, String> colOffre;

    @FXML
    private TableColumn<Paiement, String> colDate;

    @FXML
    private TableColumn<Paiement, Void> colAction;

    @FXML
    private Button btnRetourOffres;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortComboBox;

    private final paiementService paiementService = new paiementService();
    private ObservableList<Paiement> paiementsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Assurez-vous que btnRetourOffres est correctement lié ici.
        System.out.println(btnRetourOffres); // Vérifiez si btnRetourOffres est non null

        // Définir les données des colonnes
        colUser.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser() != null ? cellData.getValue().getUser().getName() : "N/A"));
        colOffre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOffre() != null ? cellData.getValue().getOffre().getName() : "N/A"));
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPaymentDate() != null ? cellData.getValue().getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : ""));

        chargerPaiements();
        ajouterBoutonsActions();

        // Initialiser le ComboBox avec des options de tri
        sortComboBox.getItems().addAll("Date croissante", "Date décroissante");

        // Ajout de l'écouteur pour la recherche dynamique
        searchField.textProperty().addListener((observable, oldValue, newValue) -> appliquerFiltrageEtTri());

        // Ajout de l'écouteur pour le changement de critère de tri
        sortComboBox.valueProperty().addListener((observable, oldValue, newValue) -> appliquerFiltrageEtTri());
    }

    private void chargerPaiements() {
        try {
            List<Paiement> paiements = paiementService.recuperer();
            paiementsList.setAll(paiements);
            tablePaiements.setItems(paiementsList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des paiements.");
        }
    }

    private void appliquerFiltrageEtTri() {
        String recherche = searchField.getText().toLowerCase();
        String critereTri = sortComboBox.getValue();

        // Filtrer par nom d'utilisateur
        List<Paiement> resultat = paiementsList.stream()
                .filter(paiement -> paiement.getUser() != null &&
                        paiement.getUser().getName().toLowerCase().contains(recherche))
                .collect(Collectors.toList());

        // Tri
        if (critereTri != null) {
            switch (critereTri) {
                case "Date croissante":
                    resultat.sort((p1, p2) -> p1.getPaymentDate().compareTo(p2.getPaymentDate()));
                    break;
                case "Date décroissante":
                    resultat.sort((p1, p2) -> p2.getPaymentDate().compareTo(p1.getPaymentDate()));
                    break;
            }
        }

        tablePaiements.getItems().setAll(resultat);
    }

    private void ajouterBoutonsActions() {
        colAction.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Paiement, Void> call(final TableColumn<Paiement, Void> param) {
                return new TableCell<>() {
                    private final Button deleteBtn = new Button("🗑 Supprimer");

                    {
                        deleteBtn.getStyleClass().add("supprimer-btn");

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
                            setGraphic(new HBox(10, deleteBtn));
                        }
                    }
                };
            }
        });
    }

    @FXML
    private void allerVersOffres() {
        try {
            // Charger le fichier FXML de la vue "AfficherOffre"
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/afficherOffre.fxml"));
            AnchorPane newPage = loader.load();

            // Obtenir la scène actuelle
            Stage currentStage = (Stage) btnRetourOffres.getScene().getWindow();

            // Créer une nouvelle scène avec la vue "AfficherOffre"
            Scene newScene = new Scene(newPage);

            // Changer la scène pour la nouvelle vue
            currentStage.setScene(newScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du changement de vue.");
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
