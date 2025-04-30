<<<<<<< Updated upstream:main/java/tn/esprit/tests/SignUpTest.java
package tn.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.services.UserService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class SignUpTest extends Application {
    private final UserService userService = UserService.getInstance();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/auth/signup.fxml"));
        Parent root = loader.load();

        // Fix: Cast the controller
        tn.esprit.controllers.auth.SignUp signUpController =
                (tn.esprit.controllers.auth.SignUp) loader.getController();

        primaryStage.setTitle("SignUp Test");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        // Optional: Auto-fill test data
        TextField emailField = (TextField) root.lookup("#emailField");
        PasswordField passwordField = (PasswordField) root.lookup("#passwordField");
        PasswordField confirmPasswordField = (PasswordField) root.lookup("#confirmPasswordField");
        TextField nameField = (TextField) root.lookup("#nameField");
        Label errorLabel = (Label) root.lookup("#errorLabel");

        System.out.println("=== TEST INSTRUCTIONS ===");
        System.out.println("1. Fill fields and click 'Sign Up'");
    }

    public static void testSignUpLogic() {
        UserService userService = UserService.getInstance();
        String testEmail = "test@esprit.tn";

        // Cleanup old test user (if any)
        List<User> users = userService.getAll();
        users.stream()
                .filter(u -> u.getEmail().equals(testEmail))
                .forEach(u -> userService.supprimer(u.getId())); // Now works (int/long)

        // Test valid signup
        User newUser = new User(testEmail, "Test User", BCrypt.hashpw("123456", BCrypt.gensalt()));
        userService.ajouter(newUser);

        // Verify using getByEmail
        User dbUser = userService.getByEmail(testEmail);
        if (dbUser != null) {
            System.out.println("✅ Signup SUCCESS! User: " + dbUser.getEmail());
        } else {
            System.out.println("❌ Signup FAILED!");
        }
    }
=======
package tn.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.services.VerificationServer;
import java.io.IOException;

public class SignUpTest extends Application {

    public static void main(String[] args) {
        try {
            // Corrected method call
            VerificationServer.start();  // Changed from startServer()
            launch(args);
        } catch (IOException e) {
            System.err.println("Failed to start verification server:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/interfaces/auth/signup.fxml")
            );

            TextField emailField = (TextField) root.lookup("#emailField");
            if (emailField != null) {
                emailField.setText("test@esprit.tn");
            }

            primaryStage.setTitle("SignUp Test");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();

        } catch (IOException e) {
            showError("FXML Loading Error",
                    "Failed to load signup.fxml\n" +
                            "Path: src/main/resources/interfaces/auth/signup.fxml\n" +
                            "Error: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        VerificationServer.stop();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
>>>>>>> Stashed changes:src/main/java/tn/esprit/tests/SignUpTest.java
}