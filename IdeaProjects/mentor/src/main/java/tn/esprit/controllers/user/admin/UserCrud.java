package tn.esprit.controllers.user.admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.controllers.auth.SignUp;
import tn.esprit.entities.User;
import tn.esprit.services.UserService;

import java.io.IOException;

public class UserCrud {
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Long> idColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, Void> actionsColumn;
    @FXML private Button addButton;
    @FXML private Button refreshButton;

    private final UserService userService = UserService.getInstance();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        setupActionsColumn();
        refreshTable();
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");

                    {
                        editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-pref-width: 60;");
                        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-pref-width: 70;");
                        editBtn.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            showEditDialog(user);
                        });
                        deleteBtn.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            confirmAndDeleteUser(user);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new HBox(5, editBtn, deleteBtn));
                        }
                    }
                };
            }
        });
    }

    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/auth/signup.fxml"));
            Parent root = loader.load();

            SignUp signUpController = loader.getController();
            signUpController.setRedirectTarget("/interfaces/user/admin/user_crud.fxml");

            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Add New User");
        } catch (IOException e) {
            showAlert("Error", "Could not open signup page: " + e.getMessage());
        }
    }

    private void confirmAndDeleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete User: " + user.getName());
        alert.setContentText("Are you sure you want to delete this user?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userService.supprimer(user.getId().intValue());
                refreshTable();
                showAlert("Success", "User deleted successfully!");
            }
        });
    }

    @FXML
    private void handleRefresh() {
        refreshTable();
    }

    private void refreshTable() {
        try {
            usersTable.getSelectionModel().clearSelection();
            ObservableList<User> users = FXCollections.observableArrayList(userService.getAll());
            usersTable.getItems().clear();
            usersTable.setItems(users);
            usersTable.refresh();
            Platform.runLater(() -> {
                idColumn.setPrefWidth(idColumn.getWidth());
                emailColumn.setPrefWidth(emailColumn.getWidth());
                nameColumn.setPrefWidth(nameColumn.getWidth());
            });
        } catch (Exception e) {
            showAlert("Error", "Failed to refresh data: " + e.getMessage());
        }
    }

    private void showEditDialog(User user) {
        try {
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Edit User");
            dialog.setHeaderText("Editing: " + user.getName());

            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField emailField = new TextField(user.getEmail());
            TextField nameField = new TextField(user.getName());
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Leave blank to keep current");

            grid.add(new Label("Email:"), 0, 0);
            grid.add(emailField, 1, 0);
            grid.add(new Label("Name:"), 0, 1);
            grid.add(nameField, 1, 1);
            grid.add(new Label("Password:"), 0, 2);
            grid.add(passwordField, 1, 2);

            dialog.getDialogPane().setContent(grid);
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    user.setEmail(emailField.getText());
                    user.setName(nameField.getText());
                    if (!passwordField.getText().isEmpty()) {
                        user.setPassword(passwordField.getText());
                    }
                    return user;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(updatedUser -> {
                if (updatedUser.getId() != null) {
                    try {
                        userService.modifier(updatedUser);
                        refreshTable();
                        showAlert("Success", "User updated successfully!");
                    } catch (Exception e) {
                        showAlert("Error", "Failed to update user: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            showAlert("Error", "Failed to edit user: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}