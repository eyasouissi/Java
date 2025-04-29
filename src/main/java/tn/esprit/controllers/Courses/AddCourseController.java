package tn.esprit.controllers.Courses;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tn.esprit.entities.Category;
import tn.esprit.entities.Courses;
import tn.esprit.services.CoursesService;

import java.util.List;

public class AddCourseController {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextField pointsField;
    @FXML private CheckBox premiumCheckBox;
    @FXML private CheckBox publishedCheckBox;
    @FXML private TextField tutorField;

    @FXML private Label titleError;
    @FXML private Label descriptionError;
    @FXML private Label categoryError;
    @FXML private Label pointsError;
    @FXML private Label tutorError;

    private CoursesController parentController;
    private final CoursesService coursesService = CoursesService.getInstance();

    @FXML
    private void initialize() {
        configureCategoryComboBox();
        setupValidations();
    }

    private void configureCategoryComboBox() {
        categoryComboBox.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getName() : "";
            }

            @Override
            public Category fromString(String string) {
                return categoryComboBox.getItems().stream()
                        .filter(cat -> cat.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    public void setCategories(List<Category> categories) {
        categoryComboBox.getItems().setAll(categories);
    }

    private void setupValidations() {
        pointsField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                pointsField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    private void handleSave() {
        if (validateForm()) {
            try {
                Courses newCourse = createCourseFromInput();
                coursesService.ajouter(newCourse);
                notifyParentController();
                closeWindow();
            } catch (Exception e) {
                showAlert("Erreur Critique", "Échec de la création du cours: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private Courses createCourseFromInput() {
        Courses course = new Courses();
        course.setTitle(titleField.getText().trim());
        course.setDescription(descriptionField.getText().trim());
        course.setCategory(categoryComboBox.getValue());
        course.setProgressPointsRequired(Integer.parseInt(pointsField.getText()));
        course.setIsPremium(premiumCheckBox.isSelected());
        course.setIsPublished(publishedCheckBox.isSelected());
        course.setTutorName(tutorField.getText().trim());
        return course;
    }

    private boolean validateForm() {
        clearValidationStyles();
        boolean isValid = true;

        if (titleField.getText().trim().isEmpty()) {
            markError(titleField, titleError, "Le titre est obligatoire");
            isValid = false;
        }

        if (descriptionField.getText().trim().isEmpty()) {
            markError(descriptionField, descriptionError, "La description est obligatoire");
            isValid = false;
        }

        if (categoryComboBox.getValue() == null) {
            markError(categoryComboBox, categoryError, "Sélectionnez une catégorie");
            isValid = false;
        }

        if (pointsField.getText().isEmpty()) {
            markError(pointsField, pointsError, "Les points sont obligatoires");
            isValid = false;
        } else {
            try {
                int points = Integer.parseInt(pointsField.getText());
                if (points < 0 || points > 1000) {
                    markError(pointsField, pointsError, "Entre 0 et 1000");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                markError(pointsField, pointsError, "Nombre invalide");
                isValid = false;
            }
        }

        if (tutorField.getText().trim().isEmpty()) {
            markError(tutorField, tutorError, "Le tuteur est obligatoire");
            isValid = false;
        }

        if (!isValid) {
            showAlert("Formulaire Invalide", "Veuillez corriger les erreurs", Alert.AlertType.WARNING);
        }

        return isValid;
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private void notifyParentController() {
        if (parentController != null) {
            parentController.refreshCoursesList();
        }
    }

    private void markError(Control field, Label errorLabel, String message) {
        field.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1.5;");
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void clearValidationStyles() {
        Control[] fields = {titleField, descriptionField, categoryComboBox, pointsField, tutorField};
        for (Control field : fields) {
            field.setStyle("");
        }

        Label[] errorLabels = {titleError, descriptionError, categoryError, pointsError, tutorError};
        for (Label label : errorLabels) {
            label.setVisible(false);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setParentController(CoursesController controller) {
        this.parentController = controller;
    }
}