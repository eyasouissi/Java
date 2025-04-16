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

public class EditCategoryController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField iconField;
    @FXML private CheckBox isActiveCheckBox;
    @FXML private ImageView iconPreview;
    @FXML private Label noIconLabel;

    private Category category;
    private CategoryController categoryController;
    private final CategoryService categoryService = new CategoryService();
    private final FileChooser fileChooser = new FileChooser();

    public void setCategory(Category category) {
        this.category = category;
        populateFields();
    }

    public void setCategoryController(CategoryController categoryController) {
        this.categoryController = categoryController;
    }

    @FXML
    public void initialize() {
        // Configuration du FileChooser
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        // Listener pour l'aperçu de l'icône
        iconField.textProperty().addListener((obs, oldVal, newVal) -> updateIconPreview(newVal));
    }

    private void populateFields() {
        nameField.setText(category.getName());
        descriptionField.setText(category.getDescription());
        iconField.setText(category.getIcon());
        isActiveCheckBox.setSelected(category.getIsActive());
        updateIconPreview(category.getIcon());
    }

    @FXML
    private void handleBrowse() {
        File file = fileChooser.showOpenDialog(iconField.getScene().getWindow());
        if (file != null) {
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
    private void handleSave() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String icon = iconField.getText().trim();
        boolean isActive = isActiveCheckBox.isSelected();

        // Validation
        if (name.isEmpty() || description.isEmpty()) {
            showAlert("Champs requis", "Les champs Nom et Description sont obligatoires",
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            category.setName(name);
            category.setDescription(description);
            category.setIcon(icon);
            category.setIsActive(isActive);

            categoryService.modifier(category);

            categoryController.loadCategories();
            closeWindow();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
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


    @FXML
    private void handleReset() {
        int choice = new Alert(Alert.AlertType.CONFIRMATION,
                "Réinitialiser tous les changements?",
                ButtonType.YES, ButtonType.NO)
                .showAndWait()
                .filter(btn -> btn == ButtonType.YES)
                .map(btn -> 1)
                .orElse(0);

        if (choice == 1) {
            populateFields();
        }
    }
}