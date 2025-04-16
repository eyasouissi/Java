package tn.esprit.controllers.auth;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.esprit.controllers.user.ProfileController;
import tn.esprit.entities.User;
import javafx.scene.Node;
import java.io.IOException;

public class MainController {
    @FXML
    private Label welcomeLabel;
    private User currentUser;

    public void initializeWithUser(User user, String message) {
        this.currentUser = user;
        if (welcomeLabel != null && user != null) {
            welcomeLabel.setText("Welcome, " + user.getName() + "! " + message);
        }
    }
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/auth/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/user/profile.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the user data
            ProfileController profileController = loader.getController();
            profileController.setUserData(currentUser);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("User Profile");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}