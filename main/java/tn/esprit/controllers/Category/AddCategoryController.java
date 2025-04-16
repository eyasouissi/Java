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
import java.time.LocalDateTime;

public class AddCategoryController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField iconField;
    @FXML private CheckBox isActiveCheckBox;
    @FXML private ImageView iconPreview;
    @FXML private Label noIconLabel;
    @FXML private Label nameErrorLabel;
    @FXML private Label descErrorLabel;
    @FXML private Label iconErrorLabel;

    private CategoryController categoryController;
    private final CategoryService categoryService = new CategoryService();
    private final FileChooser fileChooser = new FileChooser();

    public void setCategoryController(CategoryController categoryController) {
        this.categoryController = categoryController;
    }

    @FXML
    public void initialize() {
        // Configuration du FileChooser
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        // Initialisation des labels d'erreur
        nameErrorLabel.setText("");
        descErrorLabel.setText("");
        iconErrorLabel.setText("");

        // Contrôles de saisie en temps réel
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateName());
        descriptionField.textProperty().addListener((obs, oldVal, newVal) -> validateDescription());
        iconField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateIconPreview(newVal);
            validateIcon();
        });
    }

    private void validateName() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            nameErrorLabel.setText("Le nom est obligatoire");
        } else if (name.length() > 50) {
            nameErrorLabel.setText("Max 50 caractères");
        } else if (!name.matches("^[\\p{L}0-9 .'-]+$")) {
            nameErrorLabel.setText("Caractères spéciaux non autorisés");
        } else {
            nameErrorLabel.setText("");
        }
    }

    private void validateDescription() {
        String desc = descriptionField.getText().trim();
        if (desc.isEmpty()) {
            descErrorLabel.setText("La description est obligatoire");
        } else if (desc.length() > 255) {
            descErrorLabel.setText("Max 255 caractères");
        } else {
            descErrorLabel.setText("");
        }
    }

    private void validateIcon() {
        String iconPath = iconField.getText().trim();
        if (!iconPath.isEmpty()) {
            try {
                new Image(iconPath); // Teste si l'image est valide
                iconErrorLabel.setText("");
            } catch (Exception e) {
                iconErrorLabel.setText("Format d'image invalide");
            }
        } else {
            iconErrorLabel.setText("Une icône est recommandée");
        }
    }

    @FXML
    private void handleBrowse() {
        File file = fileChooser.showOpenDialog(iconField.getScene().getWindow());
        if (file != null) {
            // Vérifie la taille du fichier (max 2MB)
            if (file.length() > 2 * 1024 * 1024) {
                showAlert("Fichier trop volumineux", "L'image ne doit pas dépasser 2MB", Alert.AlertType.WARNING);
                return;
            }
            iconField.setText(file.toURI().toString());
        }
    }

    private void updateIconPreview(String iconPath) {
        if (iconPath == null || iconPath.isEmpty()) {
            iconPreview.setImage(null);
            noIconLabel.setVisible(true);
            return;
        }

        try {
            Image image = new Image(iconPath);
            iconPreview.setImage(image);
            noIconLabel.setVisible(false);
        } catch (Exception e) {
            iconPreview.setImage(null);
            noIconLabel.setVisible(true);
        }
    }

    @FXML
    private void handleAdd() {
        // Valide tous les champs avant soumission
        validateName();
        validateDescription();
        validateIcon();

        if (!isFormValid()) {
            showAlert("Formulaire invalide", "Veuillez corriger les erreurs avant de soumettre", Alert.AlertType.WARNING);
            return;
        }

        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String icon = iconField.getText().trim();
        boolean isActive = isActiveCheckBox.isSelected();

            Category category = new Category(name, description, LocalDateTime.now(), isActive, icon);
            categoryService.ajouter(category);

            showAlert("Succès", "Catégorie ajoutée avec succès", Alert.AlertType.INFORMATION);
            categoryController.loadCategories();
            closeWindow();

    }

    private boolean isFormValid() {
        return nameErrorLabel.getText().isEmpty() &&
                descErrorLabel.getText().isEmpty() &&
                !nameField.getText().trim().isEmpty() &&
                !descriptionField.getText().trim().isEmpty();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}