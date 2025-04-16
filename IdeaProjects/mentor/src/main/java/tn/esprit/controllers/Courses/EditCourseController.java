package tn.esprit.controllers.Courses;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tn.esprit.entities.Category;
import tn.esprit.entities.Courses;
import tn.esprit.services.CategoryService;
import tn.esprit.services.CoursesService;

public class EditCourseController {
    @FXML private Label idLabel;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextField pointsField;
    @FXML private CheckBox premiumCheckBox;
    @FXML private TextField tutorField;
    @FXML private CheckBox publishedCheckBox;

    private Courses course;
    private CoursesController coursesController;
    private final CategoryService categoryService = new CategoryService();
    private final CoursesService coursesService = new CoursesService();

    @FXML
    private void initialize() {
        // Configurer l'affichage des catégories par leur nom
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

        categoryComboBox.getItems().setAll(categoryService.getAll());
    }

    public void setCourse(Courses course) {
        this.course = course;
        idLabel.setText(String.valueOf(course.getId()));
        titleField.setText(course.getTitle());
        descriptionField.setText(course.getDescription());
        categoryComboBox.getSelectionModel().select(course.getCategory());
        pointsField.setText(String.valueOf(course.getProgressPointsRequired()));
        premiumCheckBox.setSelected(course.getIsPremium());
        tutorField.setText(course.getTutorName());
        publishedCheckBox.setSelected(course.getIsPublished());
    }

    public void setCoursesController(CoursesController coursesController) {
        this.coursesController = coursesController;
    }

    @FXML
    private void updateCourse() {
        try {
            course.setTitle(titleField.getText());
            course.setDescription(descriptionField.getText());
            course.setCategory(categoryComboBox.getValue());
            course.setProgressPointsRequired(Integer.parseInt(pointsField.getText()));
            course.setIsPremium(premiumCheckBox.isSelected());
            course.setTutorName(tutorField.getText());
            course.setIsPublished(publishedCheckBox.isSelected());

            coursesService.modifier(course);
            coursesController.refreshCoursesList();
            closeWindow();
        } catch (Exception e) {
            showAlert("Erreur", "Veuillez vérifier les données saisies", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) idLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setParentController(CoursesController coursesController) {
        this.coursesController = coursesController;
    }
}