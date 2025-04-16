package tn.esprit.controllers.auth;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import tn.esprit.entities.User;

public class MainController {
    @FXML
    private Label welcomeLabel;

    public void initializeWithUser(User user, String additionalMessage) {
        String welcomeText = String.format("Welcome %s! (%s)\n%s",
                user.getName(),
                user.getEmail(),
                additionalMessage);
        welcomeLabel.setText(welcomeText);
    }
}