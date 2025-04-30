package tn.esprit.controllers.Admin;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AdminController {

    @FXML
    private VBox sidebar;

    @FXML
    private HBox homeItem;

    @FXML
    private HBox browseItem;

    @FXML
    private HBox coursesItem;

    @FXML
    private HBox forumItem;

    @FXML
    private HBox groupsItem;

    @FXML
    private HBox eventsItem;

    @FXML
    private HBox notificationItem;

    @FXML
    private HBox logoutItem;

    @FXML
    public void initialize() {
        setupNavItems();
    }

    private void setupNavItems() {
        HBox[] navItems = {
            homeItem, browseItem, coursesItem, forumItem,
            groupsItem, eventsItem, notificationItem
        };

        for (HBox item : navItems) {
            item.setOnMouseClicked(event -> {
                // Remove 'selected' class from all items
                for (HBox navItem : navItems) {
                    navItem.getStyleClass().remove("selected");
                }

                // Add 'selected' class to clicked item
                item.getStyleClass().add("selected");

                // Safely retrieve and print the label text
                item.getChildren().stream()
                    .filter(node -> node instanceof Label)
                    .map(node -> ((Label) node).getText())
                    .findFirst()
                    .ifPresent(text ->
                        System.out.println("Navigation item clicked: " + text)
                    );
            });
        }

        // Logout click handling
        logoutItem.setOnMouseClicked(event -> {
            System.out.println("Logout clicked");
            // Add logout logic here
        });
    }
}
