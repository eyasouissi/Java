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
}