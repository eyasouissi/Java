package tn.esprit.controllers.Courses;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.entities.Category;
import tn.esprit.entities.Courses;
import tn.esprit.services.CategoryService;
import tn.esprit.services.CoursesService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CoursesController {

    @FXML private ListView<Courses> coursesListView;
    @FXML private HBox actionButtonsBox;
    @FXML private ComboBox<Category> categoryFilterCombo;
    @FXML private CheckBox premiumFilterCheck;
    @FXML private CheckBox publishedFilterCheck;

    private final CoursesService coursesService = CoursesService.getInstance();
    private final CategoryService categoryService = CategoryService.getInstance();
    private ObservableList<Courses> allCourses = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupListView();
        setupFilters();
        loadAllCourses();
        actionButtonsBox.setVisible(false);
    }

    private void setupListView() {
        coursesListView.setCellFactory(createCourseCellFactory());
        coursesListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> actionButtonsBox.setVisible(newVal != null)
        );
    }

    private Callback<ListView<Courses>, ListCell<Courses>> createCourseCellFactory() {
        return param -> new ListCell<Courses>() {
            @Override
            protected void updateItem(Courses course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox box = new VBox(5);
                    Label title = new Label(course.getTitle());
                    title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    HBox details = new HBox(10);
                    details.getChildren().addAll(
                            new Label("Catégorie: " + (course.getCategory() != null ?
                                    course.getCategory().getName() : "Non définie")),
                            new Label("Tuteur: " + course.getTutorName()),
                            new Label("Points: " + course.getProgressPointsRequired()),
                            new Label(course.getIsPremium() ? "⭐ Premium" : "🆓 Gratuit"),
                            new Label(course.getIsPublished() ? "✅ Publié" : "❌ Non publié")
                    );
                    details.setStyle("-fx-text-fill: #555; -fx-font-size: 12px;");

                    box.getChildren().addAll(title, details);
                    setGraphic(box);
                }
            }
        };
    }

    private void setupFilters() {
        categoryFilterCombo.setCellFactory(param -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                setText(empty || category == null ? null : category.getName());
            }
        });

        categoryFilterCombo.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                setText(empty || category == null ? "Toutes les catégories" : category.getName());
            }
        });

        categoryFilterCombo.getItems().setAll(categoryService.getAll());

        categoryFilterCombo.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> filterCourses()
        );

        premiumFilterCheck.selectedProperty().addListener(
                (obs, oldVal, newVal) -> filterCourses()
        );

        publishedFilterCheck.selectedProperty().addListener(
                (obs, oldVal, newVal) -> filterCourses()
        );
    }

    private void loadAllCourses() {
        try {
            allCourses.setAll(coursesService.getAll());
            coursesListView.setItems(allCourses);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des cours: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void filterCourses() {
        List<Courses> filtered = allCourses.stream()
                .filter(course -> categoryFilterCombo.getValue() == null ||
                        (course.getCategory() != null &&
                                course.getCategory().equals(categoryFilterCombo.getValue())))
                .filter(course -> !premiumFilterCheck.isSelected() || course.getIsPremium())
                .filter(course -> !publishedFilterCheck.isSelected() || course.getIsPublished())
                .collect(Collectors.toList());

        coursesListView.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void showAddCourseView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/Courses/AddCourseView.fxml"));
            Parent root = loader.load();
            AddCourseController controller = loader.getController();
            controller.setParentController(this);
            controller.setCategories(categoryService.getAll());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un nouveau cours");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void showEditCourseView() {
        Courses selected = coursesListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaces/Courses/EditCourseView.fxml"));
                Parent root = loader.load();
                EditCourseController controller = loader.getController();
                controller.setCourse(selected);
                controller.setParentController(this);
                controller.setCategories(categoryService.getAll());

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier le cours");
                stage.show();
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un cours à modifier", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void deleteCourse() {
        Courses selected = coursesListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer le cours '" + selected.getTitle() + "' ?");
            confirmation.setContentText("Cette action est irréversible.");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        coursesService.supprimer(selected.getId());
                        allCourses.remove(selected);
                        showAlert("Succès", "Cours supprimé avec succès", Alert.AlertType.INFORMATION);
                    } catch (Exception e) {
                        showAlert("Erreur", "Échec de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            });
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un cours à supprimer", Alert.AlertType.WARNING);
        }
    }

    public void refreshCoursesList() {
        loadAllCourses();
        resetFilters();
    }

    private void resetFilters() {
        categoryFilterCombo.getSelectionModel().clearSelection();
        premiumFilterCheck.setSelected(false);
        publishedFilterCheck.setSelected(false);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}