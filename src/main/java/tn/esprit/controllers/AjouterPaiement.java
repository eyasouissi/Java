package tn.esprit.controllers;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tn.esprit.entities.Offre;
import tn.esprit.entities.Paiement;
import tn.esprit.entities.User;
import tn.esprit.services.paiementService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Button confirmerButton; // Bien annoté

    private final paiementService paiementService = new paiementService();
    private Offre offre;

    @FXML
    private void goHome() {
        // À compléter
    }

    @FXML
    private void goOffres() {
        // À compléter
    }

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
            userComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(User user) {
                    return user != null ? user.getName() : "";
                }
                @Override
                public User fromString(String string) {
                    return null;
                }
            });
            datePaiementPicker.setValue(LocalDate.now());
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

        try {
            confirmerButton.setDisable(true);
            statusLabel.setText("Préparation du paiement Stripe...");

            String paymentIntentId = createPaymentIntent(offre.getPrice());
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            String clientSecret = intent.getClientSecret();

            openStripePaymentWindow(paymentIntentId, clientSecret, offre.getPrice());

        } catch (StripeException | IOException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        } finally {
            confirmerButton.setDisable(false);
        }
    }

    private String createPaymentIntent(double amount) throws StripeException {
        long amountInCents = (long) (amount * 100);
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amountInCents);
        params.put("currency", "eur");
        params.put("description", "Achat de l'offre: " + offre.getName());

        Map<String, Object> paymentMethodOptions = new HashMap<>();
        Map<String, Object> cardOptions = new HashMap<>();
        cardOptions.put("request_three_d_secure", "automatic");
        paymentMethodOptions.put("card", cardOptions);
        params.put("payment_method_options", paymentMethodOptions);

        PaymentIntent intent = PaymentIntent.create(params);
        return intent.getId();
    }

    private void openStripePaymentWindow(String paymentIntentId, String clientSecret, double amount) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/ProcessPayment.fxml"));
        Parent root = loader.load();
        ProcessPayment processPaymentController = loader.getController();
        processPaymentController.setPaymentData(paymentIntentId, clientSecret, amount);

        processPaymentController.setOnPaymentComplete(() -> {
            try {
                LocalDateTime paymentDate = datePaiementPicker.getValue().atStartOfDay();
                Paiement paiement = new Paiement(userComboBox.getValue(), offre, paymentDate);
                paiementService.ajouter(paiement);
                statusLabel.setText("Paiement effectué avec succès !");
                statusLabel.setStyle("-fx-text-fill: green;");
            } catch (SQLException e) {
                e.printStackTrace();
                statusLabel.setText("Erreur lors de l'enregistrement du paiement : " + e.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Paiement par carte");
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    private List<User> getUsers() throws SQLException {
        return paiementService.getUsers();
    }
}
