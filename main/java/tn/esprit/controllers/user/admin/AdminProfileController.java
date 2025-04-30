package tn.esprit.controllers.user.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import tn.esprit.controllers.user.ProfileController;
import tn.esprit.entities.User;

public class AdminProfileController extends ProfileController {
    @FXML private Label adminBadgeLabel;

    @Override
    public void setUserData(User user) {
        super.setUserData(user);

        // Always show admin badge since this is the admin profile view
        adminBadgeLabel.setText("ADMIN");
        adminBadgeLabel.setTextFill(Color.RED);
        adminBadgeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    }
}