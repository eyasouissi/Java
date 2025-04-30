package tn.esprit.controllers.Courses;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tn.esprit.entities.Category;
import tn.esprit.entities.Courses;
import tn.esprit.services.CategoryService;
import tn.esprit.services.CoursesService;

public class AddCourseController {
    // Form fields
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextField pointsField;
    @FXML private CheckBox premiumCheckBox;
    @FXML private TextField tutorField;

    // Error labels
    @FXML private Label titleError;
    @FXML private Label descriptionError;
    @FXML private Label categoryError;
    @FXML private Label pointsError;
    @FXML private Label tutorError;

    private CoursesController coursesController;
    private final CategoryService categoryService = new CategoryService();
    private final CoursesService coursesService = new CoursesService();

    @FXML
    private void initialize() {
        setupCategoryComboBox();
        setupFieldValidations();
    }

    private void setupCategoryComboBox() {
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

        try {
            categoryComboBox.getItems().setAll(categoryService.getAll());
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les catégories", Alert.AlertType.ERROR);
        }
    }

    private void setupFieldValidations() {
        // Numeric validation for points field
        pointsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                pointsField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    private void saveCourse() {
        if (!validateForm()) {
            return;
        }

        try {
            Courses course = createCourseFromForm();
            coursesService.ajouter(course);
            coursesController.refreshCoursesList();
            closeWindow();
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'enregistrement du cours", Alert.AlertType.ERROR);
        }
    }

    private Courses createCourseFromForm() {
        Courses course = new Courses();
        course.setTitle(titleField.getText().trim());
        course.setDescription(descriptionField.getText().trim());
        course.setCategory(categoryComboBox.getValue());
        course.setProgressPointsRequired(Integer.parseInt(pointsField.getText()));
        course.setIsPremium(premiumCheckBox.isSelected());
        course.setTutorName(tutorField.getText().trim());
        return course;
    }

    private boolean validateForm() {
        clearFieldStyles();
        boolean isValid = true;

        // Title validation
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            setError(titleField, titleError, "Le titre est requis");
            isValid = false;
        }

        // Description validation
        if (descriptionField.getText() == null || descriptionField.getText().trim().isEmpty()) {
            setError(descriptionField, descriptionError, "La description est requise");
            isValid = false;
        }

        // Category validation
        if (categoryComboBox.getValue() == null) {
            setError(categoryComboBox, categoryError, "Veuillez sélectionner une catégorie");
            isValid = false;
        }

        // Points validation
        if (pointsField.getText().isEmpty()) {
            setError(pointsField, pointsError, "Les points sont requis");
            isValid = false;
        } else {
            try {
                int points = Integer.parseInt(pointsField.getText());
                if (points < 0) {
                    setError(pointsField, pointsError, "Les points doivent être positifs");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                setError(pointsField, pointsError, "Valeur numérique invalide");
                isValid = false;
            }
        }

        // Tutor validation
        if (tutorField.getText() == null || tutorField.getText().trim().isEmpty()) {
            setError(tutorField, tutorError, "Le nom du tuteur est requis");
            isValid = false;
        }

        if (!isValid) {
            showAlert("Formulaire invalide", "Veuillez corriger les erreurs indiquées", Alert.AlertType.WARNING);
        }

        return isValid;
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setError(Control field, Label errorLabel, String errorMessage) {
        field.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1.5; -fx-border-radius: 3;");
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
    }

    private void clearFieldStyles() {
        Control[] fields = {titleField, descriptionField, categoryComboBox, pointsField, tutorField};
        for (Control field : fields) {
            field.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 3;");
        }

        Label[] errorLabels = {titleError, descriptionError, categoryError, pointsError, tutorError};
        for (Label label : errorLabels) {
            label.setVisible(false);
        }
    }

    public void setParentController(CoursesController coursesController) {
        this.coursesController = coursesController;
    }
}