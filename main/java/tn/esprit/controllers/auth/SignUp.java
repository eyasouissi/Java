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
import java.util.Arrays;
import java.util.List;

public class SignUp {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField nameField;
    @FXML private TextField ageField;
    @FXML private Label errorLabel;
    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;
    @FXML private ComboBox<String> countryComboBox;
    @FXML private RadioButton studentRadio;
    @FXML private RadioButton tutorRadio;

    @FXML
    private ToggleGroup genderGroup = new ToggleGroup();
    @FXML
    private ToggleGroup roleGroup = new ToggleGroup();
    private String redirectTarget = "/interfaces/auth/login.fxml";
    private final UserService userService = UserService.getInstance();

    @FXML
    public void initialize() {
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);

        studentRadio.setToggleGroup(roleGroup);
        tutorRadio.setToggleGroup(roleGroup);
        studentRadio.setSelected(true);

        List<String> countries = Arrays.asList(
                "Tunisia", "Algeria", "Morocco", "Libya", "Egypt",
                "France", "Germany", "USA", "Canada", "UK"
        );
        countryComboBox.getItems().addAll(countries);
    }

    public void setRedirectTarget(String target) {
        this.redirectTarget = target;
    }

    @FXML
    public void handleSignUp(ActionEvent event) {
        String email = emailField.getText().trim();
        String plainPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String name = nameField.getText().trim();
        String gender = getSelectedGender();
        String ageText = ageField.getText().trim();
        String country = countryComboBox.getValue();
        String role = getSelectedRole();

        try {
            if (email.isEmpty() || plainPassword.isEmpty() || name.isEmpty() || ageText.isEmpty()) {
                showError("All fields are required");
                return;
            }

            if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                showError("Invalid email format");
                return;
            }

            if (!plainPassword.equals(confirmPassword)) {
                showError("Passwords don't match");
                return;
            }

            if (plainPassword.length() < 6) {
                showError("Password must be at least 6 characters");
                return;
            }

            if (userService.getByEmail(email) != null) {
                showError("Email already registered");
                return;
            }

            if (gender == null) {
                showError("Please select your gender");
                return;
            }

            if (country == null || country.isEmpty()) {
                showError("Please select a country");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageText);
                if (age < 13 || age > 120) {
                    showError("Age must be between 13 and 120");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Age must be a number");
                return;
            }

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword(plainPassword); // Kept original password handling
            newUser.setVerified(true);
            newUser.setGender(gender);
            newUser.setAge(age);
            newUser.setCountry(country);

            // Only added role selection
            newUser.getRoles().clear();
            newUser.addRole(role);

            userService.ajouter(newUser);

            showSuccess("Registration successful! Redirecting...");
            clearForm();

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> {
                                redirectToTarget();
                            });
                        }
                    },
                    2000
            );
        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
        }
    }

    private String getSelectedGender() {
        RadioButton selectedRadioButton = (RadioButton) genderGroup.getSelectedToggle();
        if (selectedRadioButton == maleRadio) {
            return "Male";
        } else if (selectedRadioButton == femaleRadio) {
            return "Female";
        }
        return null;
    }

    private String getSelectedRole() {
        RadioButton selectedRadioButton = (RadioButton) roleGroup.getSelectedToggle();
        if (selectedRadioButton == tutorRadio) {
            return "ROLE_TUTOR";
        }
        return "ROLE_STUDENT";
    }

    private void redirectToTarget() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(redirectTarget));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Cannot redirect to target view");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: green;");
    }

    private void clearForm() {
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        nameField.clear();
        ageField.clear();
        genderGroup.selectToggle(null);
        countryComboBox.getSelectionModel().clearSelection();
        studentRadio.setSelected(true);
    }

    @FXML
    public void handleLoginRedirect(ActionEvent event) {
        redirectToTarget();
    }
}