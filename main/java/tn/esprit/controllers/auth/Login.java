package tn.esprit.controllers.auth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.services.UserService;

import java.io.IOException;

public class Login {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Label errorLabel;

    private final UserService userService = UserService.getInstance();

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        try {
            if (email.isEmpty() || password.isEmpty()) {
                showError("Email and password are required");
                return;
            }

            if (!userService.verifyPassword(email, password)) {
                showError("Invalid email or password");
                return;
            }

            User user = userService.getByEmail(email);
            if (user == null) {
                showError("User not found");
                return;
            }

            if (!user.getVerified()) {
                showError("Account not verified. Please check your email.");
                return;
            }

            if (user.getRestricted()) {
                showError("Account restricted. Contact support.");
                return;
            }

            redirectToMainPage(user);

        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void redirectToMainPage(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/auth/main.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.initializeWithUser(user, "You have successfully logged in!");

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main Application");

        } catch (IOException e) {
            System.err.println("Redirect failed: " + e.getMessage());
            showError("Cannot redirect to main page");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    @FXML
    public void handleForgotPassword(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/interfaces/auth/forgot-password.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Cannot open password reset");
        }
    }

    @FXML
    public void handleSignUpRedirect(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/interfaces/auth/signup.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Cannot open registration");
        }
    }
}