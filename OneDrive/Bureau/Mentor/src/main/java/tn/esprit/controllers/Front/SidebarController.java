package tn.esprit.controllers.Front;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.controllers.user.ProfileController;
import tn.esprit.controllers.user.admin.AdminProfileController;
import tn.esprit.entities.User;

import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SidebarController {

    private User currentUser;
    private static final Map<String, String> FXML_PATHS = new HashMap<>();
    @FXML private VBox contentArea;


    static {
        FXML_PATHS.put("Home", "");
        FXML_PATHS.put("Courses", "");
        FXML_PATHS.put("Forum", "");
        FXML_PATHS.put("Groups", "");
        FXML_PATHS.put("Events", "");
        // Ajouter les autres chemins...
    }

    @FXML
    public void initialize() {
        // Initialisation si nécessaire
    }

    // Navigation directe
    @FXML
    private void navigateToHome() { loadView("Home"); }
    @FXML
    private void navigateToCourses() { loadView("Courses"); }
    @FXML
    private void navigateToForum() { loadView("Forum"); }
    @FXML
    private void navigateToGroups() { loadView("Groups"); }
    @FXML
    private void navigateToEvents() { loadView("Events"); }

    private void loadView(String viewName) {
        try {
            String fxmlPath = FXML_PATHS.get(viewName);
            if (fxmlPath != null && contentArea != null) {
                Parent content = FXMLLoader.load(getClass().getResource(fxmlPath));
                contentArea.getChildren().clear();
                contentArea.getChildren().add(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/interfaces/auth/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Logout failed: " + e.getMessage());
        }
    }

    @FXML
    private void goToProfile(MouseEvent event) {
        try {
            String fxmlPath = "/interfaces/user/profile.fxml";

            if (currentUser != null && currentUser.getRoles().contains("ROLE_ADMIN")) {
                fxmlPath = "/interfaces/user/admin/adminprofile.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (fxmlPath.contains("adminprofile")) {
                AdminProfileController adminController = loader.getController();
                adminController.setUserData(currentUser);
            } else {
                ProfileController profileController = loader.getController();
                profileController.setUserData(currentUser);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Profile");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load profile: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}