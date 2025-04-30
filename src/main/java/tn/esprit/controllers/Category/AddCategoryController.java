package tn.esprit.controllers.Category;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Category;
import tn.esprit.services.CategoryService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

public class AddCategoryController {
    // Constants
    private static final String XAMPP_UPLOAD_PATH = "C:/xampp/htdocs/uploads/";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

    // Form fields
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField iconField;
    @FXML private CheckBox isActiveCheckBox;
    @FXML private ImageView iconPreview;

    // UI elements
    @FXML private Label noIconLabel;
    @FXML private Label nameErrorLabel;
    @FXML private Label descErrorLabel;
    @FXML private Label iconErrorLabel;

    private CategoryController parentController;
    private final CategoryService categoryService = CategoryService.getInstance();
    private final FileChooser fileChooser = new FileChooser();

    @FXML
    public void initialize() {
        configureFileChooser();
        setupValidationListeners();
        resetErrorLabels();
    }

    private void configureFileChooser() {
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("Tous fichiers", "*.*")
        );
    }

    private void setupValidationListeners() {
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateName());
        descriptionField.textProperty().addListener((obs, oldVal, newVal) -> validateDescription());
        iconField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateIconPreview(newVal);
            validateIcon();
        });
    }

    private void resetErrorLabels() {
        nameErrorLabel.setText("");
        descErrorLabel.setText("");
        iconErrorLabel.setText("");
    }

    // Validation methods
    private void validateName() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            markError(nameField, nameErrorLabel, "Le nom est obligatoire");
        } else if (name.length() > 50) {
            markError(nameField, nameErrorLabel, "Max 50 caractères");
        } else {
            clearError(nameField, nameErrorLabel);
        }
    }

    private void validateDescription() {
        String desc = descriptionField.getText().trim();
        if (desc.isEmpty()) {
            markError(descriptionField, descErrorLabel, "La description est obligatoire");
        } else if (desc.length() > 255) {
            markError(descriptionField, descErrorLabel, "Max 255 caractères");
        } else {
            clearError(descriptionField, descErrorLabel);
        }
    }

    private void validateIcon() {
        String iconPath = iconField.getText().trim();
        if (!iconPath.isEmpty()) {
            try {
                new Image(iconPath); // Test if image is valid
                clearError(iconField, iconErrorLabel);
            } catch (Exception e) {
                markError(iconField, iconErrorLabel, "Format d'image invalide");
            }
        } else {
            iconErrorLabel.setText("Icône recommandée");
            iconField.setStyle("");
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        validateName();
        validateDescription();
        validateIcon();

        if (!nameErrorLabel.getText().isEmpty() ||
                !descErrorLabel.getText().isEmpty()) {
            isValid = false;
        }

        return isValid;
    }

    @FXML
    private void handleBrowse() {
        File selectedFile = fileChooser.showOpenDialog(iconField.getScene().getWindow());
        if (selectedFile != null) {
            try {
                if (!isValidImageFile(selectedFile)) {
                    showAlert("Format invalide", "Seuls les PNG/JPEG/JPG/GIF sont acceptés", Alert.AlertType.ERROR);
                    return;
                }

                if (selectedFile.length() > MAX_FILE_SIZE) {
                    showAlert("Trop volumineux", "L'image ne doit pas dépasser 2MB", Alert.AlertType.WARNING);
                    return;
                }

                File uploadDir = new File(XAMPP_UPLOAD_PATH);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                String extension = getFileExtension(selectedFile.getName());
                String uniqueFileName = "cat_" + UUID.randomUUID() + extension;
                File destination = new File(XAMPP_UPLOAD_PATH + uniqueFileName);
                Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

                String webPath = "http://localhost/uploads/" + uniqueFileName;
                iconField.setText(webPath);

            } catch (IOException e) {
                showAlert("Erreur", "Échec de l'upload: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean isValidImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif");
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    private void updateIconPreview(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            iconPreview.setImage(null);
            noIconLabel.setVisible(true);
            return;
        }

        try {
            Image image = new Image(imageUrl);
            iconPreview.setImage(image);
            noIconLabel.setVisible(false);
        } catch (Exception e) {
            iconPreview.setImage(null);
            noIconLabel.setVisible(true);
        }
    }

    @FXML
    private void handleAdd() {
        if (!validateForm()) {
            showAlert("Formulaire invalide", "Veuillez corriger les erreurs", Alert.AlertType.WARNING);
            return;
        }

        try {
            Category category = new Category();
            category.setName(nameField.getText().trim());
            category.setDescription(descriptionField.getText().trim());
            category.setCreatedAt(LocalDateTime.now());
            category.setIsActive(isActiveCheckBox.isSelected());
            category.setIcon(iconField.getText().trim());

            categoryService.ajouter(category);
            closeWindow();
            showAlert("Succès", "Catégorie ajoutée avec succès", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    // Utility methods
    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void markError(Control field, Label errorLabel, String message) {
        field.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1.5;");
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void clearError(Control field, Label errorLabel) {
        field.setStyle("");
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setParentController(CategoryController controller) {
        this.parentController = controller;
    }
}
